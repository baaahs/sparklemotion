/**
 * This file collects all of the logging tag values used
 * throughout the project. It is not generally included
 * directly, but comes along for the ride when other
 * components include brain_common.h
 *
 * The ESP32 code base standard is to use a statement like
 *
 *     ESP_LOGD(TAG, "One = %d", 1)
 *
 * where TAG is a locally defined string. To stay inline
 * with that yet also have some slightly upgraded logging
 * and centralness each .cpp should have brain_common.h in
 * its include structure and should then locally define
 * TAG near the top of the file by doing something like
 *
 *     #define TAG TAG_BRAIN
 *
 * Then you can cut and past ESP example code into your
 * module without any hassle.
 *
 * Our tags have additional internal structure so that the
 * log output remains easily readable. The start with a
 * # character which gives us some separation from the
 * stuff that ESP has added at th beginning of the log line
 * and then they are always only 6 characters long so that
 * the log output lines up nicely.
 */

#pragma once


#define TAG_NET             "#   net"
#define TAG_MAIN            "#  main"
#define TAG_BRAIN           "# brain"
#define TAG_COMMON          "#common"
#define TAG_HTTPD           "# httpd"
#define TAG_LEDREN          "#ledren"
#define TAG_SYSMON          "#sysmon"
#define TAG_MSG             "#   msg"
#define TAG_SHADER          "#shader"
#define TAG_SHDTREE         "#shdtre"
#define TAG_UI              "#    ui"
#define TAG_OTA             "#   ota"

#define TAG_ISO             "#   iso"
#define TAG_CS              "#    cs"

#define TAG_PROBE           "# probe"
#define TAG_SCREEN          "#screen"
#define TAG_SCREEN_TEST     "#scntst"