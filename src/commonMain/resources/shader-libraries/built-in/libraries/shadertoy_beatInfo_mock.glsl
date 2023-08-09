// Copy-paste this to add a mock beatInfo object to shadertoy for testing purposes
#define time iTime
#define resolution iResolution
#define BPM 125.0
// These will need to be Find-Replaced from beatInfo_beat -> beatInfo.beat
#define beatInfo_beat mod(time * (BPM /60.), 4.)
#define beatInfo_bpm BPM
#define beatInfo_intensity .5*smoothstep(1., 0., mod(beatInfo_beat, 1.) / 0.4) + .5*smoothstep(1., 0., (1. - mod(beatInfo_beat, 1.)) / 0.2)
#define beatInfo_confidence 1.0
