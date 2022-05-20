// Beat Shift

// Use this to adjust the current time by the beat intensity.

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo
uniform float beatTimeShiftAmount; // @@Slider default=.5 min=0 max=2

// @return time
// @param inTime time
float main(float inTime) {
    return inTime + beatInfo.intensity * beatTimeShiftAmount;
}