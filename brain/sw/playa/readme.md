## TODOs

TODO: Check all code which is called in a timer. I'm seeing stack overflows in the timer service, and a quick google shows that it only has a 512 byte stack so a regular printf will overflow it.

