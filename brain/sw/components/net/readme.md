# Network Component

The network component consists of two logical parts. The low level system handles the network interfaces and addressing. The message handling sits on top of that and is largely agnostic about interfaces although it does make some attempt to find a reasonable broadcast address.