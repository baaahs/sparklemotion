<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE eagle SYSTEM "eagle.dtd">
<eagle version="9.3.2">
<drawing>
<settings>
<setting alwaysvectorfont="no"/>
<setting verticaltext="up"/>
</settings>
<grid distance="0.1" unitdist="inch" unit="inch" style="lines" multiple="1" display="no" altdistance="0.01" altunitdist="inch" altunit="inch"/>
<layers>
<layer number="1" name="Top" color="4" fill="1" visible="no" active="no"/>
<layer number="2" name="Route2" color="1" fill="3" visible="no" active="no"/>
<layer number="3" name="Route3" color="4" fill="3" visible="no" active="no"/>
<layer number="4" name="Route4" color="1" fill="4" visible="no" active="no"/>
<layer number="5" name="Route5" color="4" fill="4" visible="no" active="no"/>
<layer number="6" name="Route6" color="1" fill="8" visible="no" active="no"/>
<layer number="7" name="Route7" color="4" fill="8" visible="no" active="no"/>
<layer number="8" name="Route8" color="1" fill="2" visible="no" active="no"/>
<layer number="9" name="Route9" color="4" fill="2" visible="no" active="no"/>
<layer number="10" name="Route10" color="1" fill="7" visible="no" active="no"/>
<layer number="11" name="Route11" color="4" fill="7" visible="no" active="no"/>
<layer number="12" name="Route12" color="1" fill="5" visible="no" active="no"/>
<layer number="13" name="Route13" color="4" fill="5" visible="no" active="no"/>
<layer number="14" name="Route14" color="1" fill="6" visible="no" active="no"/>
<layer number="15" name="Route15" color="4" fill="6" visible="no" active="no"/>
<layer number="16" name="Bottom" color="1" fill="1" visible="no" active="no"/>
<layer number="17" name="Pads" color="2" fill="1" visible="no" active="no"/>
<layer number="18" name="Vias" color="2" fill="1" visible="no" active="no"/>
<layer number="19" name="Unrouted" color="6" fill="1" visible="no" active="no"/>
<layer number="20" name="Dimension" color="15" fill="1" visible="no" active="no"/>
<layer number="21" name="tPlace" color="7" fill="1" visible="no" active="no"/>
<layer number="22" name="bPlace" color="7" fill="1" visible="no" active="no"/>
<layer number="23" name="tOrigins" color="15" fill="1" visible="no" active="no"/>
<layer number="24" name="bOrigins" color="15" fill="1" visible="no" active="no"/>
<layer number="25" name="tNames" color="7" fill="1" visible="no" active="no"/>
<layer number="26" name="bNames" color="7" fill="1" visible="no" active="no"/>
<layer number="27" name="tValues" color="7" fill="1" visible="no" active="no"/>
<layer number="28" name="bValues" color="7" fill="1" visible="no" active="no"/>
<layer number="29" name="tStop" color="7" fill="3" visible="no" active="no"/>
<layer number="30" name="bStop" color="7" fill="6" visible="no" active="no"/>
<layer number="31" name="tCream" color="7" fill="4" visible="no" active="no"/>
<layer number="32" name="bCream" color="7" fill="5" visible="no" active="no"/>
<layer number="33" name="tFinish" color="6" fill="3" visible="no" active="no"/>
<layer number="34" name="bFinish" color="6" fill="6" visible="no" active="no"/>
<layer number="35" name="tGlue" color="7" fill="4" visible="no" active="no"/>
<layer number="36" name="bGlue" color="7" fill="5" visible="no" active="no"/>
<layer number="37" name="tTest" color="7" fill="1" visible="no" active="no"/>
<layer number="38" name="bTest" color="7" fill="1" visible="no" active="no"/>
<layer number="39" name="tKeepout" color="4" fill="11" visible="no" active="no"/>
<layer number="40" name="bKeepout" color="1" fill="11" visible="no" active="no"/>
<layer number="41" name="tRestrict" color="4" fill="10" visible="no" active="no"/>
<layer number="42" name="bRestrict" color="1" fill="10" visible="no" active="no"/>
<layer number="43" name="vRestrict" color="2" fill="10" visible="no" active="no"/>
<layer number="44" name="Drills" color="7" fill="1" visible="no" active="no"/>
<layer number="45" name="Holes" color="7" fill="1" visible="no" active="no"/>
<layer number="46" name="Milling" color="3" fill="1" visible="no" active="no"/>
<layer number="47" name="Measures" color="7" fill="1" visible="no" active="no"/>
<layer number="48" name="Document" color="7" fill="1" visible="no" active="no"/>
<layer number="49" name="Reference" color="7" fill="1" visible="no" active="no"/>
<layer number="51" name="tDocu" color="7" fill="1" visible="no" active="no"/>
<layer number="52" name="bDocu" color="7" fill="1" visible="no" active="no"/>
<layer number="88" name="SimResults" color="9" fill="1" visible="yes" active="yes"/>
<layer number="89" name="SimProbes" color="9" fill="1" visible="yes" active="yes"/>
<layer number="90" name="Modules" color="5" fill="1" visible="yes" active="yes"/>
<layer number="91" name="Nets" color="2" fill="1" visible="yes" active="yes"/>
<layer number="92" name="Busses" color="1" fill="1" visible="yes" active="yes"/>
<layer number="93" name="Pins" color="2" fill="1" visible="yes" active="yes"/>
<layer number="94" name="Symbols" color="4" fill="1" visible="yes" active="yes"/>
<layer number="95" name="Names" color="7" fill="1" visible="yes" active="yes"/>
<layer number="96" name="Values" color="7" fill="1" visible="yes" active="yes"/>
<layer number="97" name="Info" color="7" fill="1" visible="yes" active="yes"/>
<layer number="98" name="Guide" color="6" fill="1" visible="yes" active="yes"/>
</layers>
<schematic xreflabel="%F%N/%S.%C%R" xrefpart="/%S.%C%R">
<libraries>
<library name="ESP32-WROOM-32D">
<packages>
<package name="MODULE_ESP32-WROOM-32D">
<text x="-8.2833" y="13.8419" size="1.27341875" layer="25">&gt;NAME</text>
<text x="-8.966959375" y="-15.6155" size="1.2738" layer="27">&gt;VALUE</text>
<circle x="-10.777" y="5.238" radius="0.1" width="0.3" layer="21"/>
<wire x1="-9" y1="12.75" x2="9" y2="12.75" width="0.127" layer="51"/>
<wire x1="9" y1="12.75" x2="9" y2="-12.75" width="0.127" layer="51"/>
<wire x1="9" y1="-12.75" x2="-9" y2="-12.75" width="0.127" layer="51"/>
<wire x1="-9" y1="-12.75" x2="-9" y2="12.75" width="0.127" layer="51"/>
<rectangle x1="-2.58366875" y1="-3.824909375" x2="0.5811" y2="-0.6589" layer="31"/>
<wire x1="-8.96" y1="6.45" x2="8.96" y2="6.45" width="0.127" layer="51"/>
<text x="-5.72471875" y="8.65068125" size="1.78101875" layer="51">ANTENNA</text>
<wire x1="-9" y1="6" x2="-9" y2="12.75" width="0.127" layer="21"/>
<wire x1="-9" y1="12.75" x2="9" y2="12.75" width="0.127" layer="21"/>
<wire x1="9" y1="12.75" x2="9" y2="6" width="0.127" layer="21"/>
<wire x1="-9" y1="-12" x2="-9" y2="-12.75" width="0.127" layer="21"/>
<wire x1="-9" y1="-12.75" x2="-6.5" y2="-12.75" width="0.127" layer="21"/>
<wire x1="6.5" y1="-12.75" x2="9" y2="-12.75" width="0.127" layer="21"/>
<wire x1="9" y1="-12" x2="9" y2="-12.75" width="0.127" layer="21"/>
<wire x1="-8.96" y1="6.45" x2="8.96" y2="6.45" width="0.127" layer="21"/>
<wire x1="-9.25" y1="13" x2="9.25" y2="13" width="0.05" layer="39"/>
<wire x1="9.25" y1="13" x2="9.25" y2="6" width="0.05" layer="39"/>
<wire x1="9.25" y1="6" x2="9.75" y2="6" width="0.05" layer="39"/>
<wire x1="9.75" y1="6" x2="9.75" y2="-13.5" width="0.05" layer="39"/>
<wire x1="9.75" y1="-13.5" x2="-9.75" y2="-13.5" width="0.05" layer="39"/>
<wire x1="-9.75" y1="-13.5" x2="-9.75" y2="6" width="0.05" layer="39"/>
<wire x1="-9.75" y1="6" x2="-9.25" y2="6" width="0.05" layer="39"/>
<wire x1="-9.25" y1="6" x2="-9.25" y2="13" width="0.05" layer="39"/>
<circle x="-8.277" y="5.238" radius="0.1" width="0.3" layer="51"/>
<smd name="39" x="-1" y="-2.24" dx="5" dy="5" layer="1" cream="no"/>
<smd name="1" x="-8.5" y="5.26" dx="2" dy="0.9" layer="1"/>
<smd name="2" x="-8.5" y="3.99" dx="2" dy="0.9" layer="1"/>
<smd name="3" x="-8.5" y="2.72" dx="2" dy="0.9" layer="1"/>
<smd name="4" x="-8.5" y="1.45" dx="2" dy="0.9" layer="1"/>
<smd name="5" x="-8.5" y="0.18" dx="2" dy="0.9" layer="1"/>
<smd name="6" x="-8.5" y="-1.09" dx="2" dy="0.9" layer="1"/>
<smd name="7" x="-8.5" y="-2.36" dx="2" dy="0.9" layer="1"/>
<smd name="8" x="-8.5" y="-3.63" dx="2" dy="0.9" layer="1"/>
<smd name="9" x="-8.5" y="-4.9" dx="2" dy="0.9" layer="1"/>
<smd name="10" x="-8.5" y="-6.17" dx="2" dy="0.9" layer="1"/>
<smd name="11" x="-8.5" y="-7.44" dx="2" dy="0.9" layer="1"/>
<smd name="12" x="-8.5" y="-8.71" dx="2" dy="0.9" layer="1"/>
<smd name="13" x="-8.5" y="-9.98" dx="2" dy="0.9" layer="1"/>
<smd name="14" x="-8.5" y="-11.25" dx="2" dy="0.9" layer="1"/>
<smd name="15" x="-5.715" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="16" x="-4.445" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="17" x="-3.175" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="18" x="-1.905" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="19" x="-0.635" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="20" x="0.635" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="21" x="1.905" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="22" x="3.175" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="23" x="4.445" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="24" x="5.715" y="-12.25" dx="2" dy="0.9" layer="1" rot="R90"/>
<smd name="25" x="8.5" y="-11.25" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="26" x="8.5" y="-9.98" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="27" x="8.5" y="-8.71" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="28" x="8.5" y="-7.44" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="29" x="8.5" y="-6.17" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="30" x="8.5" y="-4.9" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="31" x="8.5" y="-3.63" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="32" x="8.5" y="-2.36" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="33" x="8.5" y="-1.09" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="34" x="8.5" y="0.18" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="35" x="8.5" y="1.45" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="36" x="8.5" y="2.72" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="37" x="8.5" y="3.99" dx="2" dy="0.9" layer="1" rot="R180"/>
<smd name="38" x="8.5" y="5.26" dx="2" dy="0.9" layer="1" rot="R180"/>
</package>
</packages>
<symbols>
<symbol name="ESP32-WROOM-32D">
<wire x1="-15.24" y1="35.56" x2="15.24" y2="35.56" width="0.254" layer="94"/>
<wire x1="15.24" y1="35.56" x2="15.24" y2="-35.56" width="0.254" layer="94"/>
<wire x1="15.24" y1="-35.56" x2="-15.24" y2="-35.56" width="0.254" layer="94"/>
<wire x1="-15.24" y1="-35.56" x2="-15.24" y2="35.56" width="0.254" layer="94"/>
<text x="-15.0359" y="36.6745" size="1.783840625" layer="95">&gt;NAME</text>
<text x="-15.2867" y="-38.2081" size="1.78345" layer="96">&gt;VALUE</text>
<pin name="GND" x="17.78" y="-33.02" length="short" direction="pwr" rot="R180"/>
<pin name="3V3" x="17.78" y="33.02" length="short" direction="pwr" rot="R180"/>
<pin name="EN" x="-17.78" y="27.94" length="short" direction="in"/>
<pin name="SENSOR_VP" x="-17.78" y="20.32" length="short" direction="in"/>
<pin name="SENSOR_VN" x="-17.78" y="17.78" length="short" direction="in"/>
<pin name="IO34" x="17.78" y="-22.86" length="short" rot="R180"/>
<pin name="IO35" x="17.78" y="-25.4" length="short" rot="R180"/>
<pin name="IO33" x="17.78" y="-20.32" length="short" rot="R180"/>
<pin name="IO32" x="17.78" y="-17.78" length="short" rot="R180"/>
<pin name="IO25" x="17.78" y="-10.16" length="short" rot="R180"/>
<pin name="IO26" x="17.78" y="-12.7" length="short" rot="R180"/>
<pin name="IO27" x="17.78" y="-15.24" length="short" rot="R180"/>
<pin name="IO14" x="17.78" y="12.7" length="short" rot="R180"/>
<pin name="IO12" x="17.78" y="17.78" length="short" rot="R180"/>
<pin name="IO13" x="17.78" y="15.24" length="short" rot="R180"/>
<pin name="IO15" x="17.78" y="10.16" length="short" rot="R180"/>
<pin name="IO2" x="17.78" y="25.4" length="short" rot="R180"/>
<pin name="IO0" x="17.78" y="27.94" length="short" rot="R180"/>
<pin name="IO4" x="17.78" y="22.86" length="short" rot="R180"/>
<pin name="IO16" x="17.78" y="7.62" length="short" rot="R180"/>
<pin name="IO17" x="17.78" y="5.08" length="short" rot="R180"/>
<pin name="IO5" x="17.78" y="20.32" length="short" rot="R180"/>
<pin name="IO18" x="17.78" y="2.54" length="short" rot="R180"/>
<pin name="IO19" x="17.78" y="0" length="short" rot="R180"/>
<pin name="IO21" x="17.78" y="-2.54" length="short" rot="R180"/>
<pin name="IO22" x="17.78" y="-5.08" length="short" rot="R180"/>
<pin name="IO23" x="17.78" y="-7.62" length="short" rot="R180"/>
<pin name="RXD0" x="-17.78" y="7.62" length="short"/>
<pin name="TXD0" x="-17.78" y="10.16" length="short"/>
<pin name="SHD/SD2" x="-17.78" y="-15.24" length="short"/>
<pin name="SWP/SD3" x="-17.78" y="-17.78" length="short"/>
<pin name="SCS/CMD" x="-17.78" y="0" length="short"/>
<pin name="SCL/CLK" x="-17.78" y="-2.54" length="short" function="clk"/>
<pin name="SDO/SD0" x="-17.78" y="-5.08" length="short"/>
<pin name="SDI/SD1" x="-17.78" y="-7.62" length="short"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="ESP32-WROOM-32D" prefix="U">
<description>IC: SoC; GPIO, I2C, I2S, IR, SD, SDIO, SPI, UART; 2.7÷3.6VDC; 20dBm</description>
<gates>
<gate name="G$1" symbol="ESP32-WROOM-32D" x="0" y="0"/>
</gates>
<devices>
<device name="" package="MODULE_ESP32-WROOM-32D">
<connects>
<connect gate="G$1" pin="3V3" pad="2"/>
<connect gate="G$1" pin="EN" pad="3"/>
<connect gate="G$1" pin="GND" pad="1 15 38 39"/>
<connect gate="G$1" pin="IO0" pad="25"/>
<connect gate="G$1" pin="IO12" pad="14"/>
<connect gate="G$1" pin="IO13" pad="16"/>
<connect gate="G$1" pin="IO14" pad="13"/>
<connect gate="G$1" pin="IO15" pad="23"/>
<connect gate="G$1" pin="IO16" pad="27"/>
<connect gate="G$1" pin="IO17" pad="28"/>
<connect gate="G$1" pin="IO18" pad="30"/>
<connect gate="G$1" pin="IO19" pad="31"/>
<connect gate="G$1" pin="IO2" pad="24"/>
<connect gate="G$1" pin="IO21" pad="33"/>
<connect gate="G$1" pin="IO22" pad="36"/>
<connect gate="G$1" pin="IO23" pad="37"/>
<connect gate="G$1" pin="IO25" pad="10"/>
<connect gate="G$1" pin="IO26" pad="11"/>
<connect gate="G$1" pin="IO27" pad="12"/>
<connect gate="G$1" pin="IO32" pad="8"/>
<connect gate="G$1" pin="IO33" pad="9"/>
<connect gate="G$1" pin="IO34" pad="6"/>
<connect gate="G$1" pin="IO35" pad="7"/>
<connect gate="G$1" pin="IO4" pad="26"/>
<connect gate="G$1" pin="IO5" pad="29"/>
<connect gate="G$1" pin="RXD0" pad="34"/>
<connect gate="G$1" pin="SCL/CLK" pad="20"/>
<connect gate="G$1" pin="SCS/CMD" pad="19"/>
<connect gate="G$1" pin="SDI/SD1" pad="22"/>
<connect gate="G$1" pin="SDO/SD0" pad="21"/>
<connect gate="G$1" pin="SENSOR_VN" pad="5"/>
<connect gate="G$1" pin="SENSOR_VP" pad="4"/>
<connect gate="G$1" pin="SHD/SD2" pad="17"/>
<connect gate="G$1" pin="SWP/SD3" pad="18"/>
<connect gate="G$1" pin="TXD0" pad="35"/>
</connects>
<technologies>
<technology name="">
<attribute name="DESCRIPTION" value=" IC: SoC; GPIO, I2C, I2S, IR, SD, SDIO, SPI, UART; 2.7÷3.6VDC; 20dBm "/>
<attribute name="DIGI-KEY_PART_NUMBER" value="1904-1023-1-ND"/>
<attribute name="DIGI-KEY_PURCHASE_URL" value="https://www.digikey.com/product-detail/en/espressif-systems/ESP32-WROOM-32D/1904-1023-1-ND/9381732?utm_source=snapeda&amp;utm_medium=aggregator&amp;utm_campaign=symbol"/>
<attribute name="MF" value="Espressif Systems"/>
<attribute name="MP" value="ESP32-WROOM-32D"/>
<attribute name="PACKAGE" value="Module Espressif Systems"/>
</technology>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
<library name="frames">
<description>&lt;b&gt;Frames for Sheet and Layout&lt;/b&gt;</description>
<packages>
</packages>
<symbols>
<symbol name="DOCFIELD">
<wire x1="0" y1="0" x2="71.12" y2="0" width="0.1016" layer="94"/>
<wire x1="101.6" y1="15.24" x2="87.63" y2="15.24" width="0.1016" layer="94"/>
<wire x1="0" y1="0" x2="0" y2="5.08" width="0.1016" layer="94"/>
<wire x1="0" y1="5.08" x2="71.12" y2="5.08" width="0.1016" layer="94"/>
<wire x1="0" y1="5.08" x2="0" y2="15.24" width="0.1016" layer="94"/>
<wire x1="101.6" y1="15.24" x2="101.6" y2="5.08" width="0.1016" layer="94"/>
<wire x1="71.12" y1="5.08" x2="71.12" y2="0" width="0.1016" layer="94"/>
<wire x1="71.12" y1="5.08" x2="87.63" y2="5.08" width="0.1016" layer="94"/>
<wire x1="71.12" y1="0" x2="101.6" y2="0" width="0.1016" layer="94"/>
<wire x1="87.63" y1="15.24" x2="87.63" y2="5.08" width="0.1016" layer="94"/>
<wire x1="87.63" y1="15.24" x2="0" y2="15.24" width="0.1016" layer="94"/>
<wire x1="87.63" y1="5.08" x2="101.6" y2="5.08" width="0.1016" layer="94"/>
<wire x1="101.6" y1="5.08" x2="101.6" y2="0" width="0.1016" layer="94"/>
<wire x1="0" y1="15.24" x2="0" y2="22.86" width="0.1016" layer="94"/>
<wire x1="101.6" y1="35.56" x2="0" y2="35.56" width="0.1016" layer="94"/>
<wire x1="101.6" y1="35.56" x2="101.6" y2="22.86" width="0.1016" layer="94"/>
<wire x1="0" y1="22.86" x2="101.6" y2="22.86" width="0.1016" layer="94"/>
<wire x1="0" y1="22.86" x2="0" y2="35.56" width="0.1016" layer="94"/>
<wire x1="101.6" y1="22.86" x2="101.6" y2="15.24" width="0.1016" layer="94"/>
<text x="1.27" y="1.27" size="2.54" layer="94" font="vector">Date:</text>
<text x="12.7" y="1.27" size="2.54" layer="94" font="vector">&gt;LAST_DATE_TIME</text>
<text x="72.39" y="1.27" size="2.54" layer="94" font="vector">Sheet:</text>
<text x="86.36" y="1.27" size="2.54" layer="94" font="vector">&gt;SHEET</text>
<text x="88.9" y="11.43" size="2.54" layer="94" font="vector">REV:</text>
<text x="1.27" y="19.05" size="2.54" layer="94" font="vector">TITLE:</text>
<text x="1.27" y="11.43" size="2.54" layer="94" font="vector">Document Number:</text>
<text x="17.78" y="19.05" size="2.54" layer="94" font="vector">&gt;DRAWING_NAME</text>
</symbol>
<symbol name="TABL_L">
<wire x1="0" y1="0" x2="401.32" y2="0" width="0.4064" layer="94"/>
<wire x1="401.32" y1="0" x2="401.32" y2="266.7" width="0.4064" layer="94"/>
<wire x1="401.32" y1="266.7" x2="0" y2="266.7" width="0.4064" layer="94"/>
<wire x1="0" y1="266.7" x2="0" y2="0" width="0.4064" layer="94"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="TABL_L" prefix="FRAME" uservalue="yes">
<description>&lt;b&gt;FRAME&lt;/b&gt;&lt;p&gt;
401 x 266 mm, landscape</description>
<gates>
<gate name="G$1" symbol="TABL_L" x="0" y="0"/>
<gate name="G$2" symbol="DOCFIELD" x="299.72" y="0" addlevel="must"/>
</gates>
<devices>
<device name="">
<technologies>
<technology name=""/>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
<library name="KSZ8794CNXCC">
<packages>
<package name="QFN40P800X800X90-65N">
<wire x1="-4" y1="4" x2="4" y2="4" width="0.00085" layer="57"/>
<wire x1="4" y1="4" x2="4" y2="-4" width="0.00085" layer="57"/>
<wire x1="4" y1="-4" x2="-4" y2="-4" width="0.00085" layer="57"/>
<wire x1="-4" y1="-4" x2="-4" y2="4" width="0.00085" layer="57"/>
<rectangle x1="-1.901159375" y1="0.90055" x2="-0.9" y2="1.9" layer="31"/>
<rectangle x1="-0.50025625" y1="-0.50025625" x2="0.5" y2="0.5" layer="31"/>
<rectangle x1="-1.90325" y1="-1.90325" x2="-0.9" y2="-0.9" layer="31"/>
<rectangle x1="0.9006625" y1="-1.9014" x2="1.9" y2="-0.9" layer="31"/>
<rectangle x1="0.90126875" y1="0.90126875" x2="1.9" y2="1.9" layer="31"/>
<wire x1="-4" y1="4" x2="4" y2="4" width="0.127" layer="51"/>
<wire x1="4" y1="4" x2="4" y2="-4" width="0.127" layer="51"/>
<wire x1="4" y1="-4" x2="-4" y2="-4" width="0.127" layer="51"/>
<wire x1="-4" y1="-4" x2="-4" y2="4" width="0.127" layer="51"/>
<wire x1="-4" y1="4" x2="-3.4" y2="4" width="0.127" layer="21"/>
<wire x1="-4" y1="4" x2="-4" y2="3.4" width="0.127" layer="21"/>
<wire x1="4" y1="4" x2="4" y2="3.4" width="0.127" layer="21"/>
<wire x1="4" y1="4" x2="3.4" y2="4" width="0.127" layer="21"/>
<wire x1="-4" y1="-4" x2="-3.4" y2="-4" width="0.127" layer="21"/>
<wire x1="-4" y1="-4" x2="-4" y2="-3.4" width="0.127" layer="21"/>
<wire x1="4" y1="-4" x2="4" y2="-3.4" width="0.127" layer="21"/>
<wire x1="4" y1="-4" x2="3.4" y2="-4" width="0.127" layer="21"/>
<wire x1="-4.75" y1="4.75" x2="4.75" y2="4.75" width="0.05" layer="39"/>
<wire x1="4.75" y1="4.75" x2="4.75" y2="-4.75" width="0.05" layer="39"/>
<wire x1="4.75" y1="-4.75" x2="-4.75" y2="-4.75" width="0.05" layer="39"/>
<wire x1="-4.75" y1="-4.75" x2="-4.75" y2="4.75" width="0.05" layer="39"/>
<circle x="-4.5" y="3.5" radius="0.1" width="0.2" layer="21"/>
<text x="-4.751809375" y="4.751809375" size="1.27048125" layer="25">&gt;NAME</text>
<text x="-4.75778125" y="-6.00981875" size="1.27208125" layer="27">&gt;VALUE</text>
<polygon width="0.05" layer="41">
<vertex x="-3.9" y="3.2"/>
<vertex x="-3.9" y="3.9"/>
<vertex x="-3.2" y="3.9"/>
<vertex x="-3.2" y="3.4"/>
<vertex x="3.2" y="3.4"/>
<vertex x="3.2" y="3.9"/>
<vertex x="3.9" y="3.9"/>
<vertex x="3.9" y="3.2"/>
<vertex x="3.4" y="3.2"/>
<vertex x="3.4" y="0"/>
<vertex x="2.2" y="0"/>
<vertex x="2.2" y="2.2"/>
<vertex x="-2.2" y="2.2"/>
<vertex x="-2.2" y="0"/>
<vertex x="-3.4" y="0"/>
<vertex x="-3.4" y="3.2"/>
</polygon>
<polygon width="0.05" layer="41">
<vertex x="3.9" y="-3.2"/>
<vertex x="3.9" y="-3.9"/>
<vertex x="3.2" y="-3.9"/>
<vertex x="3.2" y="-3.4"/>
<vertex x="-3.2" y="-3.4"/>
<vertex x="-3.2" y="-3.9"/>
<vertex x="-3.9" y="-3.9"/>
<vertex x="-3.9" y="-3.2"/>
<vertex x="-3.4" y="-3.2"/>
<vertex x="-3.4" y="0"/>
<vertex x="-2.2" y="0"/>
<vertex x="-2.2" y="-2.2"/>
<vertex x="2.2" y="-2.2"/>
<vertex x="2.2" y="0"/>
<vertex x="3.4" y="0"/>
<vertex x="3.4" y="-3.2"/>
</polygon>
<polygon width="0.05" layer="43">
<vertex x="-3.9" y="3.2"/>
<vertex x="-3.9" y="3.9"/>
<vertex x="-3.2" y="3.9"/>
<vertex x="-3.2" y="3.4"/>
<vertex x="3.2" y="3.4"/>
<vertex x="3.2" y="3.9"/>
<vertex x="3.9" y="3.9"/>
<vertex x="3.9" y="3.2"/>
<vertex x="3.4" y="3.2"/>
<vertex x="3.4" y="0"/>
<vertex x="2.2" y="0"/>
<vertex x="2.2" y="2.2"/>
<vertex x="-2.2" y="2.2"/>
<vertex x="-2.2" y="0"/>
<vertex x="-3.4" y="0"/>
<vertex x="-3.4" y="3.2"/>
</polygon>
<polygon width="0.05" layer="43">
<vertex x="3.9" y="-3.2"/>
<vertex x="3.9" y="-3.9"/>
<vertex x="3.2" y="-3.9"/>
<vertex x="3.2" y="-3.4"/>
<vertex x="-3.2" y="-3.4"/>
<vertex x="-3.2" y="-3.9"/>
<vertex x="-3.9" y="-3.9"/>
<vertex x="-3.9" y="-3.2"/>
<vertex x="-3.4" y="-3.2"/>
<vertex x="-3.4" y="0"/>
<vertex x="-2.2" y="0"/>
<vertex x="-2.2" y="-2.2"/>
<vertex x="2.2" y="-2.2"/>
<vertex x="2.2" y="0"/>
<vertex x="3.4" y="0"/>
<vertex x="3.4" y="-3.2"/>
</polygon>
<smd name="1" x="-3.945" y="3" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="2" x="-3.945" y="2.6" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="3" x="-3.945" y="2.2" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="4" x="-3.945" y="1.8" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="5" x="-3.945" y="1.4" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="6" x="-3.945" y="1" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="7" x="-3.945" y="0.6" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="8" x="-3.945" y="0.2" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="9" x="-3.945" y="-0.2" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="10" x="-3.945" y="-0.6" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="11" x="-3.945" y="-1" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="12" x="-3.945" y="-1.4" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="13" x="-3.945" y="-1.8" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="14" x="-3.945" y="-2.2" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="15" x="-3.945" y="-2.6" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="16" x="-3.945" y="-3" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R180"/>
<smd name="17" x="-3" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="18" x="-2.6" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="19" x="-2.2" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="20" x="-1.8" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="21" x="-1.4" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="22" x="-1" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="23" x="-0.6" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="24" x="-0.2" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="25" x="0.2" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="26" x="0.6" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="27" x="1" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="28" x="1.4" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="29" x="1.8" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="30" x="2.2" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="31" x="2.6" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="32" x="3" y="-3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R270"/>
<smd name="33" x="3.945" y="-3" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="34" x="3.945" y="-2.6" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="35" x="3.945" y="-2.2" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="36" x="3.945" y="-1.8" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="37" x="3.945" y="-1.4" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="38" x="3.945" y="-1" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="39" x="3.945" y="-0.6" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="40" x="3.945" y="-0.2" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="41" x="3.945" y="0.2" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="42" x="3.945" y="0.6" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="43" x="3.945" y="1" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="44" x="3.945" y="1.4" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="45" x="3.945" y="1.8" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="46" x="3.945" y="2.2" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="47" x="3.945" y="2.6" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="48" x="3.945" y="3" dx="0.81" dy="0.22" layer="1" roundness="70"/>
<smd name="49" x="3" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="50" x="2.6" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="51" x="2.2" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="52" x="1.8" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="53" x="1.4" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="54" x="1" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="55" x="0.6" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="56" x="0.2" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="57" x="-0.2" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="58" x="-0.6" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="59" x="-1" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="60" x="-1.4" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="61" x="-1.8" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="62" x="-2.2" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="63" x="-2.6" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="64" x="-3" y="3.945" dx="0.81" dy="0.22" layer="1" roundness="70" rot="R90"/>
<smd name="65" x="0" y="0" dx="4.2" dy="4.2" layer="1" cream="no"/>
<pad name="P$2" x="0" y="0" drill="0.35" diameter="0.45"/>
<pad name="P$3" x="-1.4" y="1.4" drill="0.35" diameter="0.45"/>
<pad name="P$4" x="1.4" y="1.4" drill="0.35" diameter="0.45"/>
<pad name="P$5" x="1.4" y="-1.4" drill="0.35" diameter="0.45"/>
<pad name="P$6" x="-1.4" y="-1.4" drill="0.35" diameter="0.45"/>
</package>
</packages>
<symbols>
<symbol name="KSZ8794CNXCC">
<wire x1="-30.48" y1="35.56" x2="-33.02" y2="33.02" width="0.254" layer="94"/>
<wire x1="-33.02" y1="33.02" x2="-33.02" y2="-35.56" width="0.254" layer="94"/>
<wire x1="-33.02" y1="-35.56" x2="33.02" y2="-35.56" width="0.254" layer="94"/>
<wire x1="33.02" y1="-35.56" x2="33.02" y2="35.56" width="0.254" layer="94"/>
<wire x1="33.02" y1="35.56" x2="-30.48" y2="35.56" width="0.254" layer="94"/>
<text x="22.9044" y="35.6291" size="2.54493125" layer="95">&gt;NAME</text>
<text x="22.8968" y="-38.1613" size="2.544090625" layer="96">&gt;VALUE</text>
<pin name="COL4@45" x="38.1" y="10.16" length="middle" rot="R180"/>
<pin name="CRS4@44" x="38.1" y="7.62" length="middle" rot="R180"/>
<pin name="EP" x="38.1" y="-33.02" length="middle" direction="pas" rot="R180"/>
<pin name="GNDA@3" x="-38.1" y="12.7" length="middle" direction="pwr"/>
<pin name="GNDA@17" x="-17.78" y="-40.64" length="middle" direction="pwr" rot="R90"/>
<pin name="GNDA@62" x="-12.7" y="40.64" length="middle" direction="pwr" rot="R270"/>
<pin name="GNDD@22" x="-5.08" y="-40.64" length="middle" direction="pwr" rot="R90"/>
<pin name="GNDD@26" x="5.08" y="-40.64" length="middle" direction="pwr" rot="R90"/>
<pin name="GNDD@32" x="20.32" y="-40.64" length="middle" direction="pwr" rot="R90"/>
<pin name="GNDD@38" x="38.1" y="-7.62" length="middle" direction="pwr" rot="R180"/>
<pin name="GNDD@57" x="0" y="40.64" length="middle" direction="pwr" rot="R270"/>
<pin name="INTR_N@18" x="-15.24" y="-40.64" length="middle" direction="out" rot="R90"/>
<pin name="ISET@61" x="-10.16" y="40.64" length="middle" direction="pas" rot="R270"/>
<pin name="LED1_0@51" x="15.24" y="40.64" length="middle" rot="R270"/>
<pin name="LED1_1@50" x="17.78" y="40.64" length="middle" rot="R270"/>
<pin name="LED2_0@49" x="20.32" y="40.64" length="middle" rot="R270"/>
<pin name="LED2_1@48" x="38.1" y="17.78" length="middle" rot="R180"/>
<pin name="LED3_0@20" x="-10.16" y="-40.64" length="middle" rot="R90"/>
<pin name="LED3_1@19" x="-12.7" y="-40.64" length="middle" rot="R90"/>
<pin name="NC@31" x="17.78" y="-40.64" length="middle" direction="pas" rot="R90"/>
<pin name="PME@47" x="38.1" y="15.24" length="middle" rot="R180"/>
<pin name="REFCLKO@46" x="38.1" y="12.7" length="middle" rot="R180"/>
<pin name="RST_N@58" x="-2.54" y="40.64" length="middle" direction="in" rot="R270"/>
<pin name="RXC4/GRXC4@35" x="38.1" y="-15.24" length="middle" rot="R180"/>
<pin name="RXD4_0@36" x="38.1" y="-12.7" length="middle" rot="R180"/>
<pin name="RXD4_1@37" x="38.1" y="-10.16" length="middle" rot="R180"/>
<pin name="RXD4_2@40" x="38.1" y="-2.54" length="middle" rot="R180"/>
<pin name="RXD4_3@41" x="38.1" y="0" length="middle" rot="R180"/>
<pin name="RXDV4/CRSDV4/RXD4_CTL@42" x="38.1" y="2.54" length="middle" rot="R180"/>
<pin name="RXER4@43" x="38.1" y="5.08" length="middle" rot="R180"/>
<pin name="RXM1@5" x="-38.1" y="7.62" length="middle" direction="in"/>
<pin name="RXM2@9" x="-38.1" y="-2.54" length="middle" direction="in"/>
<pin name="RXM3@14" x="-38.1" y="-15.24" length="middle" direction="in"/>
<pin name="RXP1@4" x="-38.1" y="10.16" length="middle" direction="in"/>
<pin name="RXP2@8" x="-38.1" y="0" length="middle" direction="in"/>
<pin name="RXP3@13" x="-38.1" y="-12.7" length="middle" direction="in"/>
<pin name="SCL_MDC@53" x="10.16" y="40.64" length="middle" direction="in" rot="R270"/>
<pin name="SDA_MDIO@54" x="7.62" y="40.64" length="middle" rot="R270"/>
<pin name="SPIQ@52" x="12.7" y="40.64" length="middle" rot="R270"/>
<pin name="SPIS_N@55" x="5.08" y="40.64" length="middle" direction="in" rot="R270"/>
<pin name="TXC4/REFCLKI4/GTXC4@34" x="38.1" y="-17.78" length="middle" rot="R180"/>
<pin name="TXD4_0@24" x="0" y="-40.64" length="middle" direction="in" rot="R90"/>
<pin name="TXD4_1@25" x="2.54" y="-40.64" length="middle" direction="in" rot="R90"/>
<pin name="TXD4_2@28" x="10.16" y="-40.64" length="middle" direction="in" rot="R90"/>
<pin name="TXD4_3@29" x="12.7" y="-40.64" length="middle" direction="in" rot="R90"/>
<pin name="TXEN4@23" x="-2.54" y="-40.64" length="middle" direction="in" rot="R90"/>
<pin name="TXER4@30" x="15.24" y="-40.64" length="middle" direction="in" rot="R90"/>
<pin name="TXM1@7" x="-38.1" y="2.54" length="middle" direction="out"/>
<pin name="TXM2@11" x="-38.1" y="-7.62" length="middle" direction="out"/>
<pin name="TXM3@16" x="-38.1" y="-20.32" length="middle" direction="out"/>
<pin name="TXP1@6" x="-38.1" y="5.08" length="middle" direction="out"/>
<pin name="TXP2@10" x="-38.1" y="-5.08" length="middle" direction="out"/>
<pin name="TXP3@15" x="-38.1" y="-17.78" length="middle" direction="out"/>
<pin name="VDD12A@1" x="-38.1" y="17.78" length="middle" direction="pwr"/>
<pin name="VDD12D@21" x="-7.62" y="-40.64" length="middle" direction="pwr" rot="R90"/>
<pin name="VDD12D@33" x="38.1" y="-20.32" length="middle" direction="pwr" rot="R180"/>
<pin name="VDD12D@59" x="-5.08" y="40.64" length="middle" direction="pwr" rot="R270"/>
<pin name="VDDAT33@2" x="-38.1" y="15.24" length="middle" direction="pwr"/>
<pin name="VDDAT33@12" x="-38.1" y="-10.16" length="middle" direction="pwr"/>
<pin name="VDDAT33@60" x="-7.62" y="40.64" length="middle" direction="pwr" rot="R270"/>
<pin name="VDDIO@27" x="7.62" y="-40.64" length="middle" direction="pwr" rot="R90"/>
<pin name="VDDIO@39" x="38.1" y="-5.08" length="middle" direction="pwr" rot="R180"/>
<pin name="VDDIO@56" x="2.54" y="40.64" length="middle" direction="pwr" rot="R270"/>
<pin name="XI@63" x="-15.24" y="40.64" length="middle" direction="in" rot="R270"/>
<pin name="XO@64" x="-17.78" y="40.64" length="middle" direction="out" rot="R270"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="KSZ8794CNXCC" prefix="U">
<description>4-Port 10/100 Switch with RGMII/GMII, 64 VQFN 8x8x0.9mm TRAY</description>
<gates>
<gate name="G$1" symbol="KSZ8794CNXCC" x="0" y="0"/>
</gates>
<devices>
<device name="" package="QFN40P800X800X90-65N">
<connects>
<connect gate="G$1" pin="COL4@45" pad="45"/>
<connect gate="G$1" pin="CRS4@44" pad="44"/>
<connect gate="G$1" pin="EP" pad="65 P$2 P$3 P$4 P$5 P$6"/>
<connect gate="G$1" pin="GNDA@17" pad="17"/>
<connect gate="G$1" pin="GNDA@3" pad="3"/>
<connect gate="G$1" pin="GNDA@62" pad="62"/>
<connect gate="G$1" pin="GNDD@22" pad="22"/>
<connect gate="G$1" pin="GNDD@26" pad="26"/>
<connect gate="G$1" pin="GNDD@32" pad="32"/>
<connect gate="G$1" pin="GNDD@38" pad="38"/>
<connect gate="G$1" pin="GNDD@57" pad="57"/>
<connect gate="G$1" pin="INTR_N@18" pad="18"/>
<connect gate="G$1" pin="ISET@61" pad="61"/>
<connect gate="G$1" pin="LED1_0@51" pad="51"/>
<connect gate="G$1" pin="LED1_1@50" pad="50"/>
<connect gate="G$1" pin="LED2_0@49" pad="49"/>
<connect gate="G$1" pin="LED2_1@48" pad="48"/>
<connect gate="G$1" pin="LED3_0@20" pad="20"/>
<connect gate="G$1" pin="LED3_1@19" pad="19"/>
<connect gate="G$1" pin="NC@31" pad="31"/>
<connect gate="G$1" pin="PME@47" pad="47"/>
<connect gate="G$1" pin="REFCLKO@46" pad="46"/>
<connect gate="G$1" pin="RST_N@58" pad="58"/>
<connect gate="G$1" pin="RXC4/GRXC4@35" pad="35"/>
<connect gate="G$1" pin="RXD4_0@36" pad="36"/>
<connect gate="G$1" pin="RXD4_1@37" pad="37"/>
<connect gate="G$1" pin="RXD4_2@40" pad="40"/>
<connect gate="G$1" pin="RXD4_3@41" pad="41"/>
<connect gate="G$1" pin="RXDV4/CRSDV4/RXD4_CTL@42" pad="42"/>
<connect gate="G$1" pin="RXER4@43" pad="43"/>
<connect gate="G$1" pin="RXM1@5" pad="5"/>
<connect gate="G$1" pin="RXM2@9" pad="9"/>
<connect gate="G$1" pin="RXM3@14" pad="14"/>
<connect gate="G$1" pin="RXP1@4" pad="4"/>
<connect gate="G$1" pin="RXP2@8" pad="8"/>
<connect gate="G$1" pin="RXP3@13" pad="13"/>
<connect gate="G$1" pin="SCL_MDC@53" pad="53"/>
<connect gate="G$1" pin="SDA_MDIO@54" pad="54"/>
<connect gate="G$1" pin="SPIQ@52" pad="52"/>
<connect gate="G$1" pin="SPIS_N@55" pad="55"/>
<connect gate="G$1" pin="TXC4/REFCLKI4/GTXC4@34" pad="34"/>
<connect gate="G$1" pin="TXD4_0@24" pad="24"/>
<connect gate="G$1" pin="TXD4_1@25" pad="25"/>
<connect gate="G$1" pin="TXD4_2@28" pad="28"/>
<connect gate="G$1" pin="TXD4_3@29" pad="29"/>
<connect gate="G$1" pin="TXEN4@23" pad="23"/>
<connect gate="G$1" pin="TXER4@30" pad="30"/>
<connect gate="G$1" pin="TXM1@7" pad="7"/>
<connect gate="G$1" pin="TXM2@11" pad="11"/>
<connect gate="G$1" pin="TXM3@16" pad="16"/>
<connect gate="G$1" pin="TXP1@6" pad="6"/>
<connect gate="G$1" pin="TXP2@10" pad="10"/>
<connect gate="G$1" pin="TXP3@15" pad="15"/>
<connect gate="G$1" pin="VDD12A@1" pad="1"/>
<connect gate="G$1" pin="VDD12D@21" pad="21"/>
<connect gate="G$1" pin="VDD12D@33" pad="33"/>
<connect gate="G$1" pin="VDD12D@59" pad="59"/>
<connect gate="G$1" pin="VDDAT33@12" pad="12"/>
<connect gate="G$1" pin="VDDAT33@2" pad="2"/>
<connect gate="G$1" pin="VDDAT33@60" pad="60"/>
<connect gate="G$1" pin="VDDIO@27" pad="27"/>
<connect gate="G$1" pin="VDDIO@39" pad="39"/>
<connect gate="G$1" pin="VDDIO@56" pad="56"/>
<connect gate="G$1" pin="XI@63" pad="63"/>
<connect gate="G$1" pin="XO@64" pad="64"/>
</connects>
<technologies>
<technology name="">
<attribute name="DESCRIPTION" value=" 4-Port 10/100 Switch with RGMII/GMII, 64 VQFN 8x8x0.9mm T/R "/>
<attribute name="DIGI-KEY_PART_NUMBER" value="KSZ8794CNXCC-CT-ND"/>
<attribute name="DIGI-KEY_PURCHASE_URL" value="https://www.digikey.com/product-detail/en/microchip-technology/KSZ8794CNXCC-TR/KSZ8794CNXCC-CT-ND/9923662?utm_source=snapeda&amp;utm_medium=aggregator&amp;utm_campaign=symbol"/>
<attribute name="MF" value="Microchip"/>
<attribute name="MP" value="KSZ8794CNXCC-TR"/>
<attribute name="PACKAGE" value="QFN-64 Microchip"/>
</technology>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
</libraries>
<attributes>
</attributes>
<variantdefs>
</variantdefs>
<classes>
<class number="0" name="default" width="0" drill="0">
</class>
</classes>
<parts>
<part name="U1" library="ESP32-WROOM-32D" deviceset="ESP32-WROOM-32D" device=""/>
<part name="FRAME2" library="frames" deviceset="TABL_L" device=""/>
<part name="FRAME1" library="frames" deviceset="TABL_L" device=""/>
<part name="U2" library="KSZ8794CNXCC" deviceset="KSZ8794CNXCC" device=""/>
</parts>
<sheets>
<sheet>
<plain>
</plain>
<instances>
<instance part="U1" gate="G$1" x="167.64" y="170.18" smashed="yes">
<attribute name="NAME" x="152.6041" y="206.8545" size="1.783840625" layer="95"/>
<attribute name="VALUE" x="152.3533" y="131.9719" size="1.78345" layer="96"/>
</instance>
<instance part="FRAME1" gate="G$1" x="0" y="0" smashed="yes"/>
<instance part="FRAME1" gate="G$2" x="299.72" y="0" smashed="yes">
<attribute name="LAST_DATE_TIME" x="312.42" y="1.27" size="2.54" layer="94" font="vector"/>
<attribute name="SHEET" x="386.08" y="1.27" size="2.54" layer="94" font="vector"/>
<attribute name="DRAWING_NAME" x="317.5" y="19.05" size="2.54" layer="94" font="vector"/>
</instance>
</instances>
<busses>
<bus name="RMII:CLK_IN,CRS_DV,MDC,MDIO,RXD0,RXD1,TXD0,TXD1,TX_EN">
<segment>
<wire x1="208.28" y1="200.66" x2="208.28" y2="144.78" width="0.762" layer="92"/>
<label x="226.06" y="177.8" size="1.778" layer="95"/>
</segment>
<segment>
<wire x1="129.54" y1="165.1" x2="129.54" y2="160.02" width="0.762" layer="92"/>
<label x="127" y="154.94" size="1.778" layer="95"/>
</segment>
</bus>
</busses>
<nets>
<net name="CLK_IN" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO0"/>
<wire x1="185.42" y1="198.12" x2="208.28" y2="198.12" width="0.1524" layer="91"/>
</segment>
</net>
<net name="TX_EN" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO21"/>
<wire x1="185.42" y1="167.64" x2="208.28" y2="167.64" width="0.1524" layer="91"/>
</segment>
</net>
<net name="TXD0" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO19"/>
<wire x1="185.42" y1="170.18" x2="208.28" y2="170.18" width="0.1524" layer="91"/>
</segment>
</net>
<net name="TXD1" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO22"/>
<wire x1="185.42" y1="165.1" x2="208.28" y2="165.1" width="0.1524" layer="91"/>
</segment>
</net>
<net name="CRS_DV" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO27"/>
<wire x1="185.42" y1="154.94" x2="208.28" y2="154.94" width="0.1524" layer="91"/>
</segment>
</net>
<net name="RXD0" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO25"/>
<wire x1="185.42" y1="160.02" x2="208.28" y2="160.02" width="0.1524" layer="91"/>
</segment>
</net>
<net name="RXD1" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="IO26"/>
<wire x1="185.42" y1="157.48" x2="208.28" y2="157.48" width="0.1524" layer="91"/>
</segment>
</net>
<net name="MDC" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="SDO/SD0"/>
<wire x1="149.86" y1="165.1" x2="129.54" y2="165.1" width="0.1524" layer="91"/>
</segment>
</net>
<net name="MDIO" class="0">
<segment>
<pinref part="U1" gate="G$1" pin="SDI/SD1"/>
<wire x1="149.86" y1="162.56" x2="129.54" y2="162.56" width="0.1524" layer="91"/>
</segment>
</net>
</nets>
</sheet>
<sheet>
<plain>
</plain>
<instances>
<instance part="FRAME2" gate="G$1" x="0" y="0" smashed="yes"/>
<instance part="FRAME2" gate="G$2" x="299.72" y="0" smashed="yes">
<attribute name="LAST_DATE_TIME" x="312.42" y="1.27" size="2.54" layer="94" font="vector"/>
<attribute name="SHEET" x="386.08" y="1.27" size="2.54" layer="94" font="vector"/>
<attribute name="DRAWING_NAME" x="317.5" y="19.05" size="2.54" layer="94" font="vector"/>
</instance>
<instance part="U2" gate="G$1" x="185.42" y="182.88" smashed="yes">
<attribute name="NAME" x="208.3244" y="218.5091" size="2.54493125" layer="95"/>
<attribute name="VALUE" x="208.3168" y="144.7187" size="2.544090625" layer="96"/>
</instance>
</instances>
<busses>
<bus name="RMII:CLK_IN,CRS_DV,MDC,MDIO,RXD0,RXD1,TXD0,TXD1,TX_EN">
<segment>
<wire x1="172.72" y1="129.54" x2="200.66" y2="129.54" width="0.762" layer="92"/>
</segment>
<segment>
<wire x1="241.3" y1="190.5" x2="241.3" y2="167.64" width="0.762" layer="92"/>
<label x="243.84" y="177.8" size="1.778" layer="95"/>
</segment>
</bus>
</busses>
<nets>
<net name="TX_EN" class="0">
<segment>
<pinref part="U2" gate="G$1" pin="TXEN4@23"/>
<wire x1="182.88" y1="142.24" x2="182.88" y2="129.54" width="0.1524" layer="91"/>
</segment>
</net>
<net name="TXD0" class="0">
<segment>
<pinref part="U2" gate="G$1" pin="TXD4_0@24"/>
<wire x1="185.42" y1="142.24" x2="185.42" y2="129.54" width="0.1524" layer="91"/>
</segment>
</net>
<net name="TXD1" class="0">
<segment>
<pinref part="U2" gate="G$1" pin="TXD4_1@25"/>
<wire x1="187.96" y1="142.24" x2="187.96" y2="129.54" width="0.1524" layer="91"/>
</segment>
</net>
<net name="RXD0" class="0">
<segment>
<pinref part="U2" gate="G$1" pin="RXD4_0@36"/>
<wire x1="223.52" y1="170.18" x2="241.3" y2="170.18" width="0.1524" layer="91"/>
</segment>
</net>
<net name="RXD1" class="0">
<segment>
<pinref part="U2" gate="G$1" pin="RXD4_1@37"/>
<wire x1="223.52" y1="172.72" x2="241.3" y2="172.72" width="0.1524" layer="91"/>
</segment>
</net>
<net name="CRS_DV" class="0">
<segment>
<pinref part="U2" gate="G$1" pin="RXDV4/CRSDV4/RXD4_CTL@42"/>
<wire x1="223.52" y1="185.42" x2="241.3" y2="185.42" width="0.1524" layer="91"/>
</segment>
</net>
</nets>
</sheet>
</sheets>
</schematic>
</drawing>
</eagle>
