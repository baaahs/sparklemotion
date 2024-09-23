//! Embassy ESP-NOW Example
//!
//! Broadcasts, receives and sends messages via esp-now in an async way
//!
//! Because of the huge task-arena size configured this won't work on ESP32-S2

//% FEATURES: async embassy embassy-generic-timers esp-wifi esp-wifi/async esp-wifi/embassy-net esp-wifi/wifi-default esp-wifi/wifi esp-wifi/utils esp-wifi/esp-now
//% CHIPS: esp32 esp32s3 esp32c2 esp32c3 esp32c6

#![no_std]
#![no_main]
#![feature(type_alias_impl_trait)]
#![feature(async_closure)]
#![feature(array_chunks)]

extern crate alloc;

use core::{convert::Infallible, fmt::write, mem::MaybeUninit};

use alloc::format;
use byteorder::{BigEndian, ByteOrder};
use embassy_executor::Spawner;
use embassy_futures::select::{select, select3, select4, Either, Either3, Either4};
use embassy_sync::{blocking_mutex::raw::CriticalSectionRawMutex, signal::Signal};
use embassy_time::{Duration, Instant, Ticker, Timer};
use embedded_io::{ErrorType, Read, Write};
use esp_backtrace as _;
use esp_hal::{
    clock::ClockControl,
    dma::*,
    dma::{Dma, DmaPriority},
    dma_buffers, dma_descriptors,
    gpio::{GpioPin, Input, Io, Level, Output, Pull},
    peripherals::Peripherals,
    prelude::*,
    rmt::Rmt,
    rng::Rng,
    spi::{master::Spi, SpiMode},
    system::SystemControl,
    timer::{ErasedTimer, OneShotTimer, PeriodicTimer},
};
use esp_hal_embassy::InterruptExecutor;
use esp_hal_smartled::{smartLedBuffer, SmartLedsAdapter};
use esp_println::println;
use esp_wifi::{
    // esp_now::{PeerInfo, BROADCAST_ADDRESS},
    initialize,
    wifi::{
        get_ap_mac, get_sta_mac, ipv4::SocketAddr, AccessPointConfiguration, ClientConfiguration,
        Configuration, WifiApDevice, WifiController, WifiDevice, WifiDeviceMode, WifiEvent,
        WifiMode, WifiStaDevice, WifiState,
    },
    EspWifiInitFor,
};
use heapless::Vec;
use log::{info, trace};
use smart_leds::{
    colors,
    hsv::{hsv2rgb, Hsv},
    RGB, RGB8,
};
use static_cell::{make_static, StaticCell};

use embassy_net::{
    tcp::TcpSocket, udp::PacketMetadata, Config, IpAddress, IpListenEndpoint, Ipv4Address,
    Ipv4Cidr, Stack, StackResources, StaticConfigV4,
};

macro_rules! mk_static {
    ($t:ty,$val:expr) => {{
        static STATIC_CELL: static_cell::StaticCell<$t> = static_cell::StaticCell::new();
        #[deny(unused_attributes)]
        let x = STATIC_CELL.uninit().write(($val));
        x
    }};
}

const MAGIC_BYTES: [u8; 3] = [0xA1, 0x41, 0xAB];

#[main]
async fn main(spawner: Spawner) -> ! {
    esp_println::logger::init_logger_from_env();

    let peripherals = Peripherals::take();
    init_heap();
    let system = SystemControl::new(peripherals.SYSTEM);

    let clocks = ClockControl::max(system.clock_control).freeze();
    let io = Io::new(peripherals.GPIO, peripherals.IO_MUX);

    let dma = Dma::new(peripherals.DMA);
    let dma_channel = dma.spi2channel;
    let (tx_buffer, tx_descriptors, rx_buffer, rx_descriptors) = dma_buffers!(32000);
    let dma_tx_buf = DmaTxBuf::new(tx_descriptors, tx_buffer).unwrap();
    let dma_rx_buf = DmaRxBuf::new(rx_descriptors, rx_buffer).unwrap();

    let spi = Spi::new(peripherals.SPI2, 3u32.MHz(), SpiMode::Mode0, &clocks)
        .with_mosi(io.pins.gpio27)
        .with_dma(dma_channel.configure_for_async(false, DmaPriority::Priority0))
        .with_buffers(dma_tx_buf, dma_rx_buf);

    const N_LEDS: usize = 100;
    let mut ws = ws2812_async::Ws2812::<_, { 12 * N_LEDS }>::new(spi);
    let timg0 = esp_hal::timer::timg::TimerGroup::new(peripherals.TIMG0, &clocks);

    let init = initialize(
        EspWifiInitFor::Wifi,
        timg0.timer0,
        Rng::new(peripherals.RNG),
        peripherals.RADIO_CLK,
        &clocks,
    )
    .unwrap();

    let wifi = peripherals.WIFI;
    // let mut esp_now = esp_wifi::esp_now::EspNow::new(&init, wifi).unwrap();
    // println!("esp-now version {}", esp_now.get_version().unwrap());

    let (wifi_sta_interface, mut controller) =
        esp_wifi::wifi::new_with_mode(&init, wifi, WifiStaDevice).unwrap();

    #[cfg(feature = "esp32")]
    {
        let timg1 = esp_hal::timer::timg::TimerGroup::new(peripherals.TIMG1, &clocks);
        esp_hal_embassy::init(&clocks, timg1.timer0);
    }

    #[cfg(not(feature = "esp32"))]
    {
        let systimer = esp_hal::timer::systimer::SystemTimer::new(peripherals.SYSTIMER);
        esp_hal_embassy::init(
            &clocks,
            mk_static!(
                [OneShotTimer<ErasedTimer>; 1],
                [OneShotTimer::new(systimer.alarm0.into())]
            ),
        );
    }

    let seed = 1337;

    let ap_config = Config::ipv4_static(StaticConfigV4 {
        address: Ipv4Cidr::new(Ipv4Address::new(192, 168, 2, 1), 24),
        gateway: Some(Ipv4Address::from_bytes(&[192, 168, 2, 1])),
        dns_servers: Default::default(),
    });
    //let ap_config = Config::dhcpv4(Default::default());
    let sta_config = Config::dhcpv4(Default::default());
    // let sta_config = Config::ipv4_static(StaticConfigV4 {
    //     address: "192.168.2.17/32".parse().unwrap(),
    //     //gateway: Some("192.168.2.1".parse().unwrap()),
    //     gateway: None,
    //     dns_servers: heapless::Vec::new(),
    // });
    // let ap_stack = &*mk_static!(
    //     Stack<WifiDevice<'_, WifiApDevice>>,
    //     Stack::new(
    //         wifi_ap_interface,
    //         ap_config,
    //         mk_static!(StackResources<3>, StackResources::<3>::new()),
    //         seed
    //     )
    // );
    let sta_stack = &*mk_static!(
        Stack<WifiDevice<'_, WifiStaDevice>>,
        Stack::new(
            wifi_sta_interface,
            sta_config,
            mk_static!(StackResources<3>, StackResources::<3>::new()),
            seed + 1,
        )
    );

    let ssid = env!("WIFI_SSID");
    let password = env!("WIFI_PASSWORD");
    // let client_config = Configuration::Mixed(
    //     ClientConfiguration {
    //         // ssid: SSID.try_into().unwrap(),
    //         ssid: "Kevin's iPhone".try_into().unwrap(),
    //         password: "redacted".try_into().unwrap(),
    //         // password: PASSWORD.try_into().unwrap(),
    //         ..Default::default()
    //     },
    //     AccessPointConfiguration {
    //         ssid: SSID.try_into().unwrap(),
    //         ..Default::default()
    //     },
    // );
    let client_config = Configuration::Client(ClientConfiguration {
        ssid: ssid.try_into().unwrap(),
        password: password.try_into().unwrap(),
        ..Default::default()
    });

    controller.set_configuration(&client_config).unwrap();
    spawner.spawn(connection(controller)).unwrap();
    spawner.spawn(sta_task(&sta_stack)).unwrap();
    // spawner.spawn(ap_task(&ap_stack)).unwrap();

    // loop {
    //     if ap_stack.is_link_up() {
    //         break;
    //     }
    //     info!("waiting for ap stack...");
    //     Timer::after(Duration::from_millis(500)).await;
    // }
    loop {
        if sta_stack.is_link_up() {
            break;
        }

        println!("Waiting for IP...");
        Timer::after(Duration::from_millis(500)).await;
    }

    let mut led_ticker = Ticker::every(Duration::from_millis(33));
    let mut led_colors = [Hsv {
        hue: 0,
        val: 255,
        sat: 255,
    }; N_LEDS];
    let mut rbg_leds = [RGB8::default(); N_LEDS];
    let mut last_rx: Option<Instant> = None;
    let mut button_pressed = false;

    static BUTTON_PRESS_SIGNAL: StaticCell<Signal<CriticalSectionRawMutex, bool>> =
        StaticCell::new();
    let button_press_signal = &*BUTTON_PRESS_SIGNAL.init(Signal::new());

    static EXECUTOR_CORE_0: StaticCell<InterruptExecutor<0>> = StaticCell::new();
    let executor_core0 =
        InterruptExecutor::new(system.software_interrupt_control.software_interrupt0);
    let executor_core0 = EXECUTOR_CORE_0.init(executor_core0);

    let spawner = executor_core0.start(esp_hal::interrupt::Priority::Priority1);
    // listen to button interrupts on separate task so we dont miss any
    spawner
        .spawn(button_interrupt(io.pins.gpio0, button_press_signal))
        .ok();

    let mut my_sta_mac = [0u8; 6];
    get_sta_mac(&mut my_sta_mac);
    let mac = my_sta_mac;
    let brain_id = format!("{:02X}{:02X}{:02X}", mac[3], mac[4], mac[5]);

    info!("brain_id: {}", brain_id);

    let mut my_ap_mac = [0u8; 6];
    get_ap_mac(&mut my_ap_mac);

    while esp_wifi::wifi::get_sta_state() != WifiState::StaConnected {
        Timer::after_millis(1000).await;
    }
    let mut rx_meta = [PacketMetadata::EMPTY; 16];
    let mut rx_buffer = [0; 4096];
    let mut tx_meta = [PacketMetadata::EMPTY; 16];
    let mut tx_buffer = [0; 4096];
    let mut buf = [0; 4096];
    let mut socket = embassy_net::udp::UdpSocket::new(
        &sta_stack,
        &mut rx_meta,
        &mut rx_buffer,
        &mut tx_meta,
        &mut tx_buffer,
    );
    socket.bind(8003).unwrap();

    let mut broadcast: embassy_net::IpEndpoint = "255.255.255.255:8002".parse().unwrap();
    sta_stack.wait_config_up().await;
    if let Some(cfg) = sta_stack.config_v4() {
        info!("cfg: {cfg:?}");
        broadcast = (cfg.address.broadcast().unwrap(), 8002).into();
        info!("{broadcast:?}");
    }
    // let broadcast: embassy_net::IpEndpoint = "192.168.2.255:8002".parse().unwrap();

    // let mut msg = heapless::Vec::<u8, 32>::new();
    // let mut msg = [0u8; 32];
    let mut msg = VecWriter {
        buffer: Vec::<u8, 128>::new(),
    };
    write_hello_msg(&mut msg, &brain_id);

    let mut msg_id = 0i16;
    let header = Header::from_payload(msg_id, msg.buffer.as_slice());
    msg_id += 1;
    let mut msg_with_header = VecWriter {
        buffer: Vec::<u8, 128>::new(),
    };
    msg_with_header
        .write_all(header.to_bytes().as_slice())
        .unwrap();
    msg_with_header.write_all(msg.buffer.as_slice()).unwrap();
    info!("hello_msg {:x?}", &msg_with_header.buffer);

    socket
        .send_to(msg_with_header.buffer.as_slice(), broadcast)
        .await
        .unwrap();
    // loop {
    //     let (len, src) = socket.recv_from(&mut buf).await.unwrap();
    //     trace!("udp rx from {src:?}: {len:} bytes");
    //     let mut reader = &buf[..len];
    //     let header = Header::from_reader(&mut reader);
    //     if header.frame_offset == 0 && header.id % 100 == 0 {
    //         info!("{:x?}", reader);
    //         info!("udp rx header: {header:?}");
    //     }
    // }

    let mut next_led_idx = None::<usize>;
    // msg_id, initial_led_bytes
    // These are used to know where to write the incoming bytes into the LED buffer, regardless of
    // which order packages from the same message id come in.
    let mut led_parse_state = None::<(i16, usize)>;

    loop {
        let res = select4(
            socket.recv_from(&mut buf),
            Timer::after_millis(1000),
            led_ticker.next(),
            button_press_signal.wait(),
        )
        .await;

        match res {
            Either4::First(r) => {
                let Ok((recv_len, _)) = r else { continue };
                let mut reader = &buf[..recv_len];
                let header = Header::from_reader(&mut reader);
                if header.frame_offset == 0 {
                    // TODO: handle messages longer than one packet
                    let msg_type = reader[0];
                    reader = &reader[1..];
                    /*
                     * Brain Panel Shade message format:
                     * 12byte header | 0x01 (message type) | 1byte bool hasPongData | optional bytearray (int + bytes) |
                     * | bytearray shader descrption  (2-bytes: 0x01 (PIXEL type),  0x01 (encoding RGB))
                     */
                    let has_pong = reader[0] > 0;
                    reader = &reader[1..];
                    if has_pong {
                        let pong_len = i32::from_be_bytes(reader[..4].try_into().unwrap());
                        reader = &reader[4..];
                        reader = &reader[pong_len as usize..];
                    }
                    let desc_len = i32::from_be_bytes(reader[..4].try_into().unwrap());
                    reader = &reader[4..];
                    let desc = &reader[..desc_len as usize];
                    reader = &reader[desc_len as usize..];
                    led_parse_state = Some((header.id, reader.len()));
                    if desc == &[1, 1] {
                        for (i, [r, b, g]) in reader.array_chunks().enumerate() {
                            if i < rbg_leds.len() {
                                rbg_leds[i] = RGB::<u8>::new(*r, *g, *b);
                            }
                        }
                    }
                }
                // match &mut led_parse_state {
                //     Some(st) => if st.0 == header.id {},
                //     None => todo!(),
                // }
            }
            Either4::Second(r) => {}
            Either4::Third(_) => {
                // let val = if let Some(last_rx) = last_rx {
                //     let secs_since = last_rx.elapsed().as_millis() as f32 / 1000f32;
                //     if secs_since > 10. {
                //         255
                //     } else {
                //         (255f32 * ((0.25f32 - secs_since) / 0.25f32).max(0f32)) as u8
                //     }
                // } else {
                //     255
                // };
                // for (idx, c) in led_colors.iter_mut().enumerate() {
                //     c.hue = c.hue.wrapping_add(1);
                //     c.val = val;
                //     rbg_leds[idx] = hsv2rgb(*c);
                // }
                _ = ws.write(rbg_leds.clone().into_iter()).await;
            }
            Either4::Fourth(_) => {
                println!("button push");
            }
        }
    }
}

pub struct VecWriter {
    buffer: Vec<u8, 128>, // Use a heapless Vec with a fixed capacity
}

impl ErrorType for VecWriter {
    type Error = Infallible;
}
impl Write for VecWriter {
    fn write(&mut self, buf: &[u8]) -> Result<usize, Self::Error> {
        let available_space = self.buffer.capacity() - self.buffer.len();
        let write_len = buf.len().min(available_space);
        self.buffer
            .extend_from_slice(&buf[..write_len])
            .map_err(|_| ())
            .unwrap();
        Ok(write_len)
    }

    fn flush(&mut self) -> Result<(), Self::Error> {
        // No-op for this example
        Ok(())
    }
}

fn write_hello_msg(w: &mut impl Write, brain_id: &str) {
    /*
            writeByte(BRAIN_HELLO);
            writeString(brainId);
            writeNullableString(panelName);
            writeNullableString(firmwareVersion);
            writeNullableString(idfVersion);
    */
    w.write_all(&[MessageType::BrainHello as u8]).unwrap();
    write_str(w, brain_id);
    write_str_opt(w, None);
    write_str_opt(w, None);
    write_str_opt(w, None);
}

fn write_str(w: &mut impl Write, s: &str) {
    let len = s.len() as u32;
    w.write_all(len.to_be_bytes().as_slice()).unwrap();
    w.write_all(s.as_bytes()).unwrap();
}

fn write_str_opt(w: &mut impl Write, s: Option<&str>) {
    if let Some(s) = s {
        w.write_all(&[1]).unwrap();
        write_str(w, s);
    } else {
        w.write_all(&[0]).unwrap();
    }
}

#[embassy_executor::task]
async fn connection(mut controller: WifiController<'static>) {
    println!("start connection task");
    println!("Device capabilities: {:?}", controller.get_capabilities());

    println!("Starting wifi");
    controller.start().await.unwrap();
    println!("Wifi started!");

    loop {
        // match esp_wifi::wifi::get_ap_state() {
        //     WifiState::ApStarted => {
        println!("About to connect...");
        match controller.connect().await {
            Ok(_) => {
                info!("connected");
                // wait until we're no longer connected
                controller.wait_for_event(WifiEvent::StaDisconnected).await;
                println!("STA disconnected");
            }
            Err(e) => {
                println!("Failed to connect to wifi: {e:?}");
                Timer::after(Duration::from_millis(5000)).await
            }
        }
    }
}
#[embassy_executor::task]
async fn sta_task(stack: &'static Stack<WifiDevice<'static, WifiStaDevice>>) {
    stack.run().await
}

#[embassy_executor::task]
async fn button_interrupt(
    pin: GpioPin<0>,
    control: &'static Signal<CriticalSectionRawMutex, bool>,
) {
    let mut button = Input::new(pin, Pull::Up);
    println!(
        "Starting button_interrupt on core {}",
        esp_hal::get_core() as usize
    );
    loop {
        button.wait_for_falling_edge().await;
        info!("button press");
        control.signal(true);
    }
}

#[repr(u8)]
#[derive(Copy, Clone)]
enum MessageType {
    BrainHello = 0u8,
    BrainPanelShade = 1u8,
}

#[derive(Debug, Clone)]
struct Header {
    id: i16,
    frame_size: i16,
    msg_size: i32,
    frame_offset: i32,
}

const FRAGMENT_MAX: usize = 1500;
const HEADER_SIZE: usize = 12;

impl Header {
    fn from_payload(id: i16, msg: &[u8]) -> Self {
        // TODO: impl fragmentation
        Self {
            id,
            frame_size: msg.len() as i16,
            msg_size: msg.len() as i32,
            frame_offset: 0,
        }
    }
    fn to_bytes(&self) -> [u8; HEADER_SIZE] {
        let mut buf = [0u8; HEADER_SIZE];
        let mut w = buf.as_mut_slice();
        w.write_all(&self.id.to_be_bytes()).unwrap();
        w.write_all(&self.frame_size.to_be_bytes()).unwrap();
        w.write_all(&self.msg_size.to_be_bytes()).unwrap();
        w.write_all(&self.frame_offset.to_be_bytes()).unwrap();
        buf
    }
    fn from_reader(r: &mut impl Read) -> Self {
        let mut id_bytes = [0u8; 2];
        r.read_exact(&mut id_bytes).unwrap();
        let mut frame_size = [0u8; 2];
        r.read_exact(&mut frame_size).unwrap();
        let mut msg_size = [0u8; size_of::<i32>()];
        r.read_exact(&mut msg_size).unwrap();
        let mut frame_offset = [0u8; size_of::<i32>()];
        r.read_exact(&mut frame_offset).unwrap();
        Self {
            id: i16::from_be_bytes(id_bytes),
            frame_size: i16::from_be_bytes(frame_size),
            msg_size: i32::from_be_bytes(msg_size),
            frame_offset: i32::from_be_bytes(frame_offset),
        }
    }
}

/*
    enum class Type : uint8_t {
        BRAIN_HELLO,       // Brain -> Pinky|Mapper
        BRAIN_PANEL_SHADE, // Pinky -> Brain
        MAPPER_HELLO,      // Mapper -> Pinky
        BRAIN_ID_REQUEST,  // Mapper -> Brain
        BRAIN_MAPPING,
        PING,
        USE_FIRMWARE,
    };

    static const size_t FRAGMENT_MAX = 1500;
    static const size_t HEADER_SIZE = 12;

    struct Header {
        int16_t id;
        int16_t frameSize;
        int32_t msgSize;
        int32_t frameOffset;
    };

    BrainHelloMsg(const char *brainId,
            const char *panelName,
            const char *firmwareVersion,
            const char *idfVersion) {
        // Need capacity for:
        //      id byte
        //      brainId string
        //      panelName NullableString (adds 1 byte boolean)
        //      firmwareVersion string
        //      idfVersion string
        if (prepCapacity(
                1 +
                capFor(brainId) +
                capForNullable(panelName) +
                capForNullable(firmwareVersion) +
                capForNullable(idfVersion)
                )) {

            writeByte(static_cast<int>(Msg::Type::BRAIN_HELLO));
            writeString(brainId);
            writeNullableString(panelName);
            writeNullableString(firmwareVersion);
            writeNullableString(idfVersion);
        }
    }

    void writeString(const char* sz) {
        if (!sz) return;

        size_t len = strlen(sz);
        size_t xtra = capFor(sz);
        if (prepCapacity(m_used + xtra)) {
            writeInt(len);
            for ( int i = 0; i < len; i++ ) {
                m_buf[m_cursor++] = (uint8_t)sz[i];
            }
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }
*/

#[global_allocator]
static ALLOCATOR: esp_alloc::EspHeap = esp_alloc::EspHeap::empty();

fn init_heap() {
    const HEAP_SIZE: usize = 3192;
    static mut HEAP: MaybeUninit<[u8; HEAP_SIZE]> = MaybeUninit::uninit();

    unsafe {
        ALLOCATOR.init(HEAP.as_mut_ptr() as *mut u8, HEAP_SIZE);
    }
}
#[embassy_executor::task]
async fn ap_task(stack: &'static Stack<WifiDevice<'static, WifiApDevice>>) {
    stack.run().await
}

/*
    enum class Encoding : uint8_t {
        DIRECT_ARGB = 0,
        DIRECT_RGB,
        INDEXED_2,
        INDEXED_4,
        INDEXED_16
    };
*/

#[repr(u8)]
enum PixelShaderEncoding {
    DirectArgb = 0,
    DirectRgb,
    Indexed2,
    Indexed4,
    Indexed16,
}
