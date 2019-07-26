(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'klock-root-klock'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'klock-root-klock'.");
    }
    root['klock-root-klock'] = factory(typeof this['klock-root-klock'] === 'undefined' ? {} : this['klock-root-klock'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var RuntimeException_init = Kotlin.kotlin.RuntimeException_init_pdl1vj$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var RuntimeException = Kotlin.kotlin.RuntimeException;
  var Throwable = Error;
  var ensureNotNull = Kotlin.ensureNotNull;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var numberToDouble = Kotlin.numberToDouble;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var numberToInt = Kotlin.numberToInt;
  var Comparable = Kotlin.kotlin.Comparable;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var Math_0 = Math;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var endsWith = Kotlin.kotlin.text.endsWith_sgbm27$;
  var replace = Kotlin.kotlin.text.replace_r2fvfm$;
  var unboxChar = Kotlin.unboxChar;
  var abs = Kotlin.kotlin.math.abs_za3lpa$;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init;
  var NotImplementedError_init = Kotlin.kotlin.NotImplementedError;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var trim = Kotlin.kotlin.text.trim_wqw3xr$;
  var capitalize = Kotlin.kotlin.text.capitalize_pdl1vz$;
  var startsWith = Kotlin.kotlin.text.startsWith_7epoxm$;
  var drop = Kotlin.kotlin.collections.drop_ba2ldo$;
  var zip = Kotlin.kotlin.collections.zip_45mdf7$;
  var toInt = Kotlin.kotlin.text.toInt_pdl1vz$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var first = Kotlin.kotlin.text.first_gw00vp$;
  var drop_0 = Kotlin.kotlin.text.drop_6ic1pp$;
  var substringBefore = Kotlin.kotlin.text.substringBefore_8cymmc$;
  var substringAfter = Kotlin.kotlin.text.substringAfter_8cymmc$;
  var equals = Kotlin.equals;
  var Regex = Kotlin.kotlin.text.Regex;
  var StringBuilder_init_0 = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var getOrNull = Kotlin.kotlin.collections.getOrNull_yzln2o$;
  var reversed = Kotlin.kotlin.collections.reversed_7wnvza$;
  var kotlin_js_internal_DoubleCompanionObject = Kotlin.kotlin.js.internal.DoubleCompanionObject;
  var toBoxedChar = Kotlin.toBoxedChar;
  var coerceAtMost = Kotlin.kotlin.ranges.coerceAtMost_dqglrj$;
  var toIntOrNull = Kotlin.kotlin.text.toIntOrNull_pdl1vz$;
  var toDoubleOrNull = Kotlin.kotlin.text.toDoubleOrNull_pdl1vz$;
  var CharRange = Kotlin.kotlin.ranges.CharRange;
  var padStart = Kotlin.kotlin.text.padStart_vrc1nu$;
  var round = Kotlin.kotlin.math.round_14dthe$;
  var padEnd = Kotlin.kotlin.text.padEnd_vrc1nu$;
  var substring = Kotlin.kotlin.text.substring_fc3b62$;
  var throwCCE = Kotlin.throwCCE;
  DateException.prototype = Object.create(RuntimeException.prototype);
  DateException.prototype.constructor = DateException;
  DateTime$Companion$DatePart.prototype = Object.create(Enum.prototype);
  DateTime$Companion$DatePart.prototype.constructor = DateTime$Companion$DatePart;
  DayOfWeek.prototype = Object.create(Enum.prototype);
  DayOfWeek.prototype.constructor = DayOfWeek;
  KlockLocale$English.prototype = Object.create(KlockLocale.prototype);
  KlockLocale$English.prototype.constructor = KlockLocale$English;
  KlockLocale$English$Companion.prototype = Object.create(KlockLocale$English.prototype);
  KlockLocale$English$Companion.prototype.constructor = KlockLocale$English$Companion;
  Month.prototype = Object.create(Enum.prototype);
  Month.prototype.constructor = Month;
  function DateException(msg) {
    RuntimeException_init(msg, this);
    this.name = 'DateException';
  }
  DateException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DateException',
    interfaces: [RuntimeException]
  };
  function DateFormat() {
    DateFormat$Companion_getInstance();
  }
  DateFormat.prototype.tryParse_ivxn3r$ = function (str, doThrow, callback$default) {
    if (doThrow === void 0)
      doThrow = false;
    return callback$default ? callback$default(str, doThrow) : this.tryParse_ivxn3r$$default(str, doThrow);
  };
  function DateFormat$Companion() {
    DateFormat$Companion_instance = this;
    this.DEFAULT_FORMAT_n0tb0o$_0 = lazy(DateFormat$Companion$DEFAULT_FORMAT$lambda);
    this.FORMAT1_kzdi6b$_0 = lazy(DateFormat$Companion$FORMAT1$lambda);
    this.FORMAT_DATE_iki0in$_0 = lazy(DateFormat$Companion$FORMAT_DATE$lambda);
    this.FORMATS = listOf([this.DEFAULT_FORMAT, this.FORMAT1]);
  }
  Object.defineProperty(DateFormat$Companion.prototype, 'DEFAULT_FORMAT', {
    get: function () {
      return this.DEFAULT_FORMAT_n0tb0o$_0.value;
    }
  });
  Object.defineProperty(DateFormat$Companion.prototype, 'FORMAT1', {
    get: function () {
      return this.FORMAT1_kzdi6b$_0.value;
    }
  });
  Object.defineProperty(DateFormat$Companion.prototype, 'FORMAT_DATE', {
    get: function () {
      return this.FORMAT_DATE_iki0in$_0.value;
    }
  });
  DateFormat$Companion.prototype.parse_61zpoe$ = function (date) {
    var tmp$;
    var lastError = null;
    tmp$ = this.FORMATS.iterator();
    while (tmp$.hasNext()) {
      var format = tmp$.next();
      try {
        return parse(format, date);
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          lastError = e;
        }
         else
          throw e;
      }
    }
    throw ensureNotNull(lastError);
  };
  DateFormat$Companion.prototype.invoke_61zpoe$ = function (pattern) {
    return PatternDateFormat_init(pattern);
  };
  function DateFormat$Companion$DEFAULT_FORMAT$lambda() {
    return DateFormat$Companion_getInstance().invoke_61zpoe$('EEE, dd MMM yyyy HH:mm:ss z');
  }
  function DateFormat$Companion$FORMAT1$lambda() {
    return DateFormat$Companion_getInstance().invoke_61zpoe$("yyyy-MM-dd'T'HH:mm:ssXXX");
  }
  function DateFormat$Companion$FORMAT_DATE$lambda() {
    return DateFormat$Companion_getInstance().invoke_61zpoe$('yyyy-MM-dd');
  }
  DateFormat$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DateFormat$Companion_instance = null;
  function DateFormat$Companion_getInstance() {
    if (DateFormat$Companion_instance === null) {
      new DateFormat$Companion();
    }
    return DateFormat$Companion_instance;
  }
  DateFormat.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DateFormat',
    interfaces: []
  };
  function parse($receiver, str) {
    var tmp$;
    tmp$ = $receiver.tryParse_ivxn3r$(str, true);
    if (tmp$ == null) {
      throw new DateException("Not a valid format: '" + str + "' for '" + $receiver + "'");
    }
    return tmp$;
  }
  function format($receiver, date) {
    return format_1($receiver, DateTime$Companion_getInstance().fromUnix_14dthe$(date));
  }
  function format_0($receiver, date) {
    return format_1($receiver, DateTime$Companion_getInstance().fromUnix_s8cxhz$(date));
  }
  function format_1($receiver, dd) {
    return $receiver.format_j01w8f$(dd.toOffsetUnadjusted_fv8bff$(TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble(0))));
  }
  function DateTime(unixMillis) {
    DateTime$Companion_getInstance();
    this.unixMillis = unixMillis;
  }
  function DateTime$Companion() {
    DateTime$Companion_instance = this;
    this.EPOCH = new DateTime(0.0);
    this.EPOCH_INTERNAL_MILLIS_8be2vx$ = 6.21355968E13;
  }
  DateTime$Companion.prototype.invoke_4lrum3$ = function (year, month, day, hour, minute, second, milliseconds) {
    if (hour === void 0)
      hour = 0;
    if (minute === void 0)
      minute = 0;
    if (second === void 0)
      second = 0;
    if (milliseconds === void 0)
      milliseconds = 0;
    return new DateTime(DateTime$Companion_getInstance().dateToMillis_0(year.year, month.index1, day) + DateTime$Companion_getInstance().timeToMillis_0(hour, minute, second) + milliseconds);
  };
  DateTime$Companion.prototype.invoke_qw7meq$ = function (year, month, day, hour, minute, second, milliseconds) {
    if (hour === void 0)
      hour = 0;
    if (minute === void 0)
      minute = 0;
    if (second === void 0)
      second = 0;
    if (milliseconds === void 0)
      milliseconds = 0;
    return new DateTime(DateTime$Companion_getInstance().dateToMillis_0(year, month.index1, day) + DateTime$Companion_getInstance().timeToMillis_0(hour, minute, second) + milliseconds);
  };
  DateTime$Companion.prototype.invoke_ui44o2$ = function (year, month, day, hour, minute, second, milliseconds) {
    if (hour === void 0)
      hour = 0;
    if (minute === void 0)
      minute = 0;
    if (second === void 0)
      second = 0;
    if (milliseconds === void 0)
      milliseconds = 0;
    return new DateTime(DateTime$Companion_getInstance().dateToMillis_0(year, month, day) + DateTime$Companion_getInstance().timeToMillis_0(hour, minute, second) + milliseconds);
  };
  DateTime$Companion.prototype.createClamped_ui44o2$ = function (year, month, day, hour, minute, second, milliseconds) {
    if (hour === void 0)
      hour = 0;
    if (minute === void 0)
      minute = 0;
    if (second === void 0)
      second = 0;
    if (milliseconds === void 0)
      milliseconds = 0;
    var clampedMonth = clamp_1(month, 1, 12);
    return this.createUnchecked_ui44o2$(year, clampedMonth, clamp_1(day, 1, Month$Companion_getInstance().invoke_za3lpa$(month).days_za3lpa$(year)), clamp_1(hour, 0, 23), clamp_1(minute, 0, 59), clamp_1(second, 0, 59), milliseconds);
  };
  DateTime$Companion.prototype.createAdjusted_ui44o2$ = function (year, month, day, hour, minute, second, milliseconds) {
    if (hour === void 0)
      hour = 0;
    if (minute === void 0)
      minute = 0;
    if (second === void 0)
      second = 0;
    if (milliseconds === void 0)
      milliseconds = 0;
    var dy = year;
    var dm = month;
    var dd = day;
    var th = hour;
    var tm = minute;
    var ts = second;
    tm = tm + cycleSteps(ts, 0, 59) | 0;
    ts = cycle(ts, 0, 59);
    th = th + cycleSteps(tm, 0, 59) | 0;
    tm = cycle(tm, 0, 59);
    dd = dd + cycleSteps(th, 0, 23) | 0;
    th = cycle(th, 0, 23);
    while (true) {
      var dup = Month$Companion_getInstance().invoke_za3lpa$(dm).days_za3lpa$(dy);
      dm = dm + cycleSteps(dd, 1, dup) | 0;
      dd = cycle(dd, 1, dup);
      dy = dy + cycleSteps(dm, 1, 12) | 0;
      dm = cycle(dm, 1, 12);
      if (cycle(dd, 1, Month$Companion_getInstance().invoke_za3lpa$(dm).days_za3lpa$(dy)) === dd) {
        break;
      }
    }
    return this.createUnchecked_ui44o2$(dy, dm, dd, th, tm, ts, milliseconds);
  };
  DateTime$Companion.prototype.createUnchecked_ui44o2$ = function (year, month, day, hour, minute, second, milliseconds) {
    if (hour === void 0)
      hour = 0;
    if (minute === void 0)
      minute = 0;
    if (second === void 0)
      second = 0;
    if (milliseconds === void 0)
      milliseconds = 0;
    return new DateTime(DateTime$Companion_getInstance().dateToMillisUnchecked_cub51b$(year, month, day) + DateTime$Companion_getInstance().timeToMillisUnchecked_0(hour, minute, second) + milliseconds);
  };
  DateTime$Companion.prototype.invoke_s8cxhz$ = function (unix) {
    return this.fromUnix_s8cxhz$(unix);
  };
  DateTime$Companion.prototype.invoke_14dthe$ = function (unix) {
    return this.fromUnix_14dthe$(unix);
  };
  DateTime$Companion.prototype.fromUnix_14dthe$ = function (unix) {
    return new DateTime(unix);
  };
  DateTime$Companion.prototype.fromUnix_s8cxhz$ = function (unix) {
    return this.fromUnix_14dthe$(unix.toNumber());
  };
  DateTime$Companion.prototype.fromString_61zpoe$ = function (str) {
    return DateFormat$Companion_getInstance().parse_61zpoe$(str);
  };
  DateTime$Companion.prototype.parse_61zpoe$ = function (str) {
    return DateFormat$Companion_getInstance().parse_61zpoe$(str);
  };
  DateTime$Companion.prototype.now = function () {
    return new DateTime(KlockInternal_getInstance().currentTime);
  };
  DateTime$Companion.prototype.nowLocal = function () {
    return DateTimeTz$Companion_getInstance().nowLocal();
  };
  DateTime$Companion.prototype.nowUnix = function () {
    return KlockInternal_getInstance().currentTime;
  };
  DateTime$Companion.prototype.nowUnixLong = function () {
    return Kotlin.Long.fromNumber(KlockInternal_getInstance().currentTime);
  };
  function DateTime$Companion$DatePart(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function DateTime$Companion$DatePart_initFields() {
    DateTime$Companion$DatePart_initFields = function () {
    };
    DateTime$Companion$DatePart$Year_instance = new DateTime$Companion$DatePart('Year', 0);
    DateTime$Companion$DatePart$DayOfYear_instance = new DateTime$Companion$DatePart('DayOfYear', 1);
    DateTime$Companion$DatePart$Month_instance = new DateTime$Companion$DatePart('Month', 2);
    DateTime$Companion$DatePart$Day_instance = new DateTime$Companion$DatePart('Day', 3);
  }
  var DateTime$Companion$DatePart$Year_instance;
  function DateTime$Companion$DatePart$Year_getInstance() {
    DateTime$Companion$DatePart_initFields();
    return DateTime$Companion$DatePart$Year_instance;
  }
  var DateTime$Companion$DatePart$DayOfYear_instance;
  function DateTime$Companion$DatePart$DayOfYear_getInstance() {
    DateTime$Companion$DatePart_initFields();
    return DateTime$Companion$DatePart$DayOfYear_instance;
  }
  var DateTime$Companion$DatePart$Month_instance;
  function DateTime$Companion$DatePart$Month_getInstance() {
    DateTime$Companion$DatePart_initFields();
    return DateTime$Companion$DatePart$Month_instance;
  }
  var DateTime$Companion$DatePart$Day_instance;
  function DateTime$Companion$DatePart$Day_getInstance() {
    DateTime$Companion$DatePart_initFields();
    return DateTime$Companion$DatePart$Day_instance;
  }
  DateTime$Companion$DatePart.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DatePart',
    interfaces: [Enum]
  };
  function DateTime$Companion$DatePart$values() {
    return [DateTime$Companion$DatePart$Year_getInstance(), DateTime$Companion$DatePart$DayOfYear_getInstance(), DateTime$Companion$DatePart$Month_getInstance(), DateTime$Companion$DatePart$Day_getInstance()];
  }
  DateTime$Companion$DatePart.values = DateTime$Companion$DatePart$values;
  function DateTime$Companion$DatePart$valueOf(name) {
    switch (name) {
      case 'Year':
        return DateTime$Companion$DatePart$Year_getInstance();
      case 'DayOfYear':
        return DateTime$Companion$DatePart$DayOfYear_getInstance();
      case 'Month':
        return DateTime$Companion$DatePart$Month_getInstance();
      case 'Day':
        return DateTime$Companion$DatePart$Day_getInstance();
      default:throwISE('No enum constant com.soywiz.klock.DateTime.Companion.DatePart.' + name);
    }
  }
  DateTime$Companion$DatePart.valueOf_61zpoe$ = DateTime$Companion$DatePart$valueOf;
  DateTime$Companion.prototype.dateToMillisUnchecked_cub51b$ = function (year, month, day) {
    return ((new Year(year)).daysSinceOne + Month$Companion_getInstance().invoke_za3lpa$(month).daysToStart_za3lpa$(year) + day - 1 | 0) * 86400000 - this.EPOCH_INTERNAL_MILLIS_8be2vx$;
  };
  DateTime$Companion.prototype.timeToMillisUnchecked_0 = function (hour, minute, second) {
    return hour * 3600000 + minute * 60000 + second * 1000;
  };
  DateTime$Companion.prototype.dateToMillis_0 = function (year, month, day) {
    var tmp$;
    Month$Companion_getInstance().checked_za3lpa$(month);
    tmp$ = Month$Companion_getInstance().invoke_za3lpa$(month).days_za3lpa$(year);
    if (!(1 <= day && day <= tmp$))
      throw new DateException('Day ' + day + ' not valid for year=' + year + ' and month=' + month);
    return this.dateToMillisUnchecked_cub51b$(year, month, day);
  };
  DateTime$Companion.prototype.timeToMillis_0 = function (hour, minute, second) {
    if (!(0 <= hour && hour <= 23))
      throw new DateException('Hour ' + hour + ' not in 0..23');
    if (!(0 <= minute && minute <= 59))
      throw new DateException('Minute ' + minute + ' not in 0..59');
    if (!(0 <= second && second <= 59))
      throw new DateException('Second ' + second + ' not in 0..59');
    return this.timeToMillisUnchecked_0(hour, minute, second);
  };
  DateTime$Companion.prototype.getDatePart_2pm4tv$ = function (millis, part) {
    var tmp$;
    var totalDays = numberToInt(millis / 86400000);
    var year = Year$Companion_getInstance().fromDays_za3lpa$(totalDays);
    if (part === DateTime$Companion$DatePart$Year_getInstance())
      return year.year;
    var isLeap = year.isLeap;
    var startYearDays = year.daysSinceOne;
    var dayOfYear = 1 + (totalDays - startYearDays) | 0;
    if (part === DateTime$Companion$DatePart$DayOfYear_getInstance())
      return dayOfYear;
    var tmp$_0;
    if ((tmp$ = Month$Companion_getInstance().fromDayOfYear_fzusl$(dayOfYear, isLeap)) != null)
      tmp$_0 = tmp$;
    else {
      throw IllegalStateException_init(('Invalid dayOfYear=' + dayOfYear + ', isLeap=' + isLeap).toString());
    }
    var month = tmp$_0;
    if (part === DateTime$Companion$DatePart$Month_getInstance())
      return month.index1;
    var dayOfMonth = dayOfYear - month.daysToStart_6taknv$(isLeap) | 0;
    if (part === DateTime$Companion$DatePart$Day_getInstance())
      return dayOfMonth;
    throw IllegalStateException_init('Invalid DATE_PART'.toString());
  };
  DateTime$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DateTime$Companion_instance = null;
  function DateTime$Companion_getInstance() {
    if (DateTime$Companion_instance === null) {
      new DateTime$Companion();
    }
    return DateTime$Companion_instance;
  }
  Object.defineProperty(DateTime.prototype, 'yearOneMillis', {
    get: function () {
      return DateTime$Companion_getInstance().EPOCH_INTERNAL_MILLIS_8be2vx$ + this.unixMillis;
    }
  });
  Object.defineProperty(DateTime.prototype, 'localOffset', {
    get: function () {
      return TimezoneOffset$Companion_getInstance().local_mw5vjr$(new DateTime(this.unixMillisDouble));
    }
  });
  Object.defineProperty(DateTime.prototype, 'unixMillisDouble', {
    get: function () {
      return this.unixMillis;
    }
  });
  Object.defineProperty(DateTime.prototype, 'unixMillisLong', {
    get: function () {
      return Kotlin.Long.fromNumber(this.unixMillisDouble);
    }
  });
  Object.defineProperty(DateTime.prototype, 'year', {
    get: function () {
      return new Year(this.yearInt);
    }
  });
  Object.defineProperty(DateTime.prototype, 'yearInt', {
    get: function () {
      return DateTime$Companion_getInstance().getDatePart_2pm4tv$(this.yearOneMillis, DateTime$Companion$DatePart$Year_getInstance());
    }
  });
  Object.defineProperty(DateTime.prototype, 'month', {
    get: function () {
      return Month$Companion_getInstance().get_za3lpa$(this.month1);
    }
  });
  Object.defineProperty(DateTime.prototype, 'month0', {
    get: function () {
      return this.month1 - 1 | 0;
    }
  });
  Object.defineProperty(DateTime.prototype, 'month1', {
    get: function () {
      return DateTime$Companion_getInstance().getDatePart_2pm4tv$(this.yearOneMillis, DateTime$Companion$DatePart$Month_getInstance());
    }
  });
  Object.defineProperty(DateTime.prototype, 'yearMonth', {
    get: function () {
      return YearMonth$Companion_getInstance().invoke_wk05xp$(this.year, this.month);
    }
  });
  Object.defineProperty(DateTime.prototype, 'dayOfMonth', {
    get: function () {
      return DateTime$Companion_getInstance().getDatePart_2pm4tv$(this.yearOneMillis, DateTime$Companion$DatePart$Day_getInstance());
    }
  });
  Object.defineProperty(DateTime.prototype, 'dayOfWeek', {
    get: function () {
      return DayOfWeek$Companion_getInstance().get_za3lpa$(this.dayOfWeekInt);
    }
  });
  Object.defineProperty(DateTime.prototype, 'dayOfWeekInt', {
    get: function () {
      return numberToInt((this.yearOneMillis / 86400000 + 1) % 7);
    }
  });
  Object.defineProperty(DateTime.prototype, 'dayOfYear', {
    get: function () {
      return DateTime$Companion_getInstance().getDatePart_2pm4tv$(this.yearOneMillis, DateTime$Companion$DatePart$DayOfYear_getInstance());
    }
  });
  Object.defineProperty(DateTime.prototype, 'hours', {
    get: function () {
      return numberToInt(this.yearOneMillis / 3600000 % 24);
    }
  });
  Object.defineProperty(DateTime.prototype, 'minutes', {
    get: function () {
      return numberToInt(this.yearOneMillis / 60000 % 60);
    }
  });
  Object.defineProperty(DateTime.prototype, 'seconds', {
    get: function () {
      return numberToInt(this.yearOneMillis / 1000 % 60);
    }
  });
  Object.defineProperty(DateTime.prototype, 'milliseconds', {
    get: function () {
      return numberToInt(this.yearOneMillis % 1000);
    }
  });
  Object.defineProperty(DateTime.prototype, 'localUnadjusted', {
    get: function () {
      return DateTimeTz$Companion_getInstance().local_rq74cp$(this, this.localOffset);
    }
  });
  DateTime.prototype.toOffsetUnadjusted_fv8bff$ = function (offset) {
    return this.toOffsetUnadjusted_q6c6ai$(get_offset(offset));
  };
  DateTime.prototype.toOffsetUnadjusted_q6c6ai$ = function (offset) {
    return DateTimeTz$Companion_getInstance().local_rq74cp$(this, offset);
  };
  Object.defineProperty(DateTime.prototype, 'local', {
    get: function () {
      return DateTimeTz$Companion_getInstance().utc_rq74cp$(this, this.localOffset);
    }
  });
  DateTime.prototype.toOffset_fv8bff$ = function (offset) {
    return this.toOffset_q6c6ai$(get_offset(offset));
  };
  DateTime.prototype.toOffset_q6c6ai$ = function (offset) {
    return DateTimeTz$Companion_getInstance().utc_rq74cp$(this, offset);
  };
  DateTime.prototype.plus_glepj8$ = function (delta) {
    return this.add_5wr77w$(delta.totalMonths, 0.0);
  };
  DateTime.prototype.plus_5gml0z$ = function (delta) {
    return this.add_5wr77w$(delta.totalMonths, delta.totalMilliseconds);
  };
  DateTime.prototype.plus_fv8bff$ = function (delta) {
    return this.add_5wr77w$(0, delta.milliseconds);
  };
  DateTime.prototype.minus_glepj8$ = function (delta) {
    return this.plus_glepj8$(delta.unaryMinus());
  };
  DateTime.prototype.minus_5gml0z$ = function (delta) {
    return this.plus_5gml0z$(delta.unaryMinus());
  };
  DateTime.prototype.minus_fv8bff$ = function (delta) {
    return this.plus_fv8bff$(delta.unaryMinus());
  };
  DateTime.prototype.minus_mw5vjr$ = function (other) {
    var $receiver = this.unixMillisDouble - other.unixMillisDouble;
    return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
  };
  DateTime.prototype.compareTo_11rb$ = function (other) {
    return Kotlin.compareTo(this.unixMillis, other.unixMillis);
  };
  DateTime.prototype.add_5wr77w$ = function (deltaMonths, deltaMilliseconds) {
    if (deltaMonths === 0 && deltaMilliseconds === 0.0)
      return this;
    else if (deltaMonths === 0)
      return new DateTime(this.unixMillis + deltaMilliseconds);
    else {
      var year = this.year;
      var month = this.month.index1;
      var day = this.dayOfMonth;
      var i = month - 1 + deltaMonths | 0;
      if (i >= 0) {
        month = i % 12 + 1 | 0;
        year = year.plus_za3lpa$(i / 12 | 0);
      }
       else {
        month = 12 + (i + 1 | 0) % 12 | 0;
        year = year.plus_za3lpa$((i - 11 | 0) / 12 | 0);
      }
      var days = Month$Companion_getInstance().invoke_za3lpa$(month).days_ccxljp$(year);
      if (day > days)
        day = days;
      return new DateTime(DateTime$Companion_getInstance().dateToMillisUnchecked_cub51b$(year.year, month, day) + this.yearOneMillis % 86400000 + deltaMilliseconds);
    }
  };
  DateTime.prototype.add_e89ho5$ = function (dateSpan, timeSpan) {
    return this.add_5wr77w$(dateSpan.totalMonths, timeSpan.milliseconds);
  };
  DateTime.prototype.copyDayOfMonth_4lrum3$ = function (year, month, dayOfMonth, hours, minutes, seconds, milliseconds) {
    if (year === void 0)
      year = this.year;
    if (month === void 0)
      month = this.month;
    if (dayOfMonth === void 0)
      dayOfMonth = this.dayOfMonth;
    if (hours === void 0)
      hours = this.hours;
    if (minutes === void 0)
      minutes = this.minutes;
    if (seconds === void 0)
      seconds = this.seconds;
    if (milliseconds === void 0)
      milliseconds = this.milliseconds;
    return DateTime$Companion_getInstance().invoke_4lrum3$(year, month, dayOfMonth, hours, minutes, seconds, milliseconds);
  };
  DateTime.prototype.format_cgtbg3$ = function (format) {
    return format_1(format, this);
  };
  DateTime.prototype.format_61zpoe$ = function (format) {
    return format_1(DateFormat$Companion_getInstance().invoke_61zpoe$(format), this);
  };
  DateTime.prototype.toString_61zpoe$ = function (format) {
    return format_1(DateFormat$Companion_getInstance().invoke_61zpoe$(format), this);
  };
  DateTime.prototype.toString_cgtbg3$ = function (format) {
    return format_1(format, this);
  };
  DateTime.prototype.toString = function () {
    return format_1(DateFormat$Companion_getInstance().DEFAULT_FORMAT, this);
  };
  DateTime.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DateTime',
    interfaces: [Comparable]
  };
  DateTime.prototype.unbox = function () {
    return this.unixMillis;
  };
  DateTime.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.unixMillis) | 0;
    return result;
  };
  DateTime.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.unixMillis, other.unixMillis))));
  };
  function max(a, b) {
    var tmp$ = DateTime$Companion_getInstance();
    var a_0 = a.unixMillis;
    var b_0 = b.unixMillis;
    return tmp$.fromUnix_14dthe$(Math_0.max(a_0, b_0));
  }
  function min(a, b) {
    var tmp$ = DateTime$Companion_getInstance();
    var a_0 = a.unixMillis;
    var b_0 = b.unixMillis;
    return tmp$.fromUnix_14dthe$(Math_0.min(a_0, b_0));
  }
  function clamp($receiver, min, max) {
    if ($receiver.compareTo_11rb$(min) < 0)
      return min;
    else if ($receiver.compareTo_11rb$(max) > 0)
      return max;
    else
      return $receiver;
  }
  function DateTimeRange(from, to, inclusive) {
    this.from = from;
    this.to = to;
    this.inclusive = inclusive;
    this.duration_ot22cs$_0 = lazy(DateTimeRange$duration$lambda(this));
    this.span_ot5r3q$_0 = lazy(DateTimeRange$span$lambda(this));
  }
  Object.defineProperty(DateTimeRange.prototype, 'duration', {
    get: function () {
      return this.duration_ot22cs$_0.value;
    }
  });
  Object.defineProperty(DateTimeRange.prototype, 'span', {
    get: function () {
      return this.span_ot5r3q$_0.value;
    }
  });
  DateTimeRange.prototype.contains_mw5vjr$ = function (date) {
    var tmp$;
    var unix = date.unixMillisDouble;
    var from = this.from.unixMillisDouble;
    var to = this.to.unixMillisDouble;
    if (unix < from)
      return false;
    if (this.inclusive)
      tmp$ = unix <= to;
    else
      tmp$ = unix < to;
    return tmp$;
  };
  function DateTimeRange$duration$lambda(this$DateTimeRange) {
    return function () {
      return this$DateTimeRange.to.minus_mw5vjr$(this$DateTimeRange.from);
    };
  }
  function DateTimeRange$span$lambda(this$DateTimeRange) {
    return function () {
      var reverse = this$DateTimeRange.to.compareTo_11rb$(this$DateTimeRange.from) < 0;
      var rfrom = !reverse ? this$DateTimeRange.from : this$DateTimeRange.to;
      var rto = !reverse ? this$DateTimeRange.to : this$DateTimeRange.from;
      var years = 0;
      var months = 0;
      var pivot = rfrom;
      var diffYears = rto.year.minus_ccxljp$(pivot.year);
      pivot = pivot.plus_glepj8$(new MonthSpan(12 * diffYears | 0));
      years = years + diffYears | 0;
      if (pivot.compareTo_11rb$(rto) > 0) {
        pivot = pivot.minus_glepj8$(new MonthSpan(12 * 1 | 0));
        years = years - 1 | 0;
      }
      while (true) {
        var t = pivot.plus_glepj8$(new MonthSpan(1));
        if (t.compareTo_11rb$(rto) < 0) {
          months = months + 1 | 0;
          pivot = t;
        }
         else {
          break;
        }
      }
      var out = new DateTimeSpan((new MonthSpan(12 * years | 0)).plus_glepj8$(new MonthSpan(months)), rto.minus_mw5vjr$(pivot));
      return reverse ? out.unaryMinus() : out;
    };
  }
  DateTimeRange.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DateTimeRange',
    interfaces: []
  };
  DateTimeRange.prototype.component1 = function () {
    return this.from;
  };
  DateTimeRange.prototype.component2 = function () {
    return this.to;
  };
  DateTimeRange.prototype.component3 = function () {
    return this.inclusive;
  };
  DateTimeRange.prototype.copy_yn6b1d$ = function (from, to, inclusive) {
    return new DateTimeRange(from === void 0 ? this.from : from, to === void 0 ? this.to : to, inclusive === void 0 ? this.inclusive : inclusive);
  };
  DateTimeRange.prototype.toString = function () {
    return 'DateTimeRange(from=' + Kotlin.toString(this.from) + (', to=' + Kotlin.toString(this.to)) + (', inclusive=' + Kotlin.toString(this.inclusive)) + ')';
  };
  DateTimeRange.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.from) | 0;
    result = result * 31 + Kotlin.hashCode(this.to) | 0;
    result = result * 31 + Kotlin.hashCode(this.inclusive) | 0;
    return result;
  };
  DateTimeRange.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.from, other.from) && Kotlin.equals(this.to, other.to) && Kotlin.equals(this.inclusive, other.inclusive)))));
  };
  function rangeTo($receiver, other) {
    return new DateTimeRange($receiver, other, true);
  }
  function until($receiver, other) {
    return new DateTimeRange($receiver, other, false);
  }
  function DateTimeSpan(monthSpan, timeSpan) {
    this.monthSpan = monthSpan;
    this.timeSpan = timeSpan;
    this.computed_a87s7m$_0 = lazy(DateTimeSpan$computed$lambda(this));
  }
  DateTimeSpan.prototype.unaryMinus = function () {
    return new DateTimeSpan(this.monthSpan.unaryMinus(), this.timeSpan.unaryMinus());
  };
  DateTimeSpan.prototype.unaryPlus = function () {
    return new DateTimeSpan(this.monthSpan.unaryPlus(), this.timeSpan.unaryPlus());
  };
  DateTimeSpan.prototype.plus_fv8bff$ = function (other) {
    return new DateTimeSpan(this.monthSpan, this.timeSpan.plus_fv8bff$(other));
  };
  DateTimeSpan.prototype.plus_glepj8$ = function (other) {
    return new DateTimeSpan(this.monthSpan.plus_glepj8$(other), this.timeSpan);
  };
  DateTimeSpan.prototype.plus_5gml0z$ = function (other) {
    return new DateTimeSpan(this.monthSpan.plus_glepj8$(other.monthSpan), this.timeSpan.plus_fv8bff$(other.timeSpan));
  };
  DateTimeSpan.prototype.minus_fv8bff$ = function (other) {
    return this.plus_fv8bff$(other.unaryMinus());
  };
  DateTimeSpan.prototype.minus_glepj8$ = function (other) {
    return this.plus_glepj8$(other.unaryMinus());
  };
  DateTimeSpan.prototype.minus_5gml0z$ = function (other) {
    return this.plus_5gml0z$(other.unaryMinus());
  };
  DateTimeSpan.prototype.times_3p81yu$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.DateTimeSpan.times_3p81yu$', wrapFunction(function () {
    var numberToDouble = Kotlin.numberToDouble;
    return function (times) {
      return this.times_14dthe$(numberToDouble(times));
    };
  }));
  DateTimeSpan.prototype.div_3p81yu$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.DateTimeSpan.div_3p81yu$', wrapFunction(function () {
    var numberToDouble = Kotlin.numberToDouble;
    return function (times) {
      return this.times_14dthe$(1.0 / numberToDouble(times));
    };
  }));
  DateTimeSpan.prototype.times_14dthe$ = function (times) {
    return new DateTimeSpan(new MonthSpan(numberToInt(this.monthSpan.totalMonths * numberToDouble(times))), this.timeSpan.times_14dthe$(times));
  };
  Object.defineProperty(DateTimeSpan.prototype, 'totalYears', {
    get: function () {
      return get_totalYears(this.monthSpan);
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'totalMonths', {
    get: function () {
      return this.monthSpan.totalMonths;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'totalMilliseconds', {
    get: function () {
      return this.timeSpan.milliseconds;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'years', {
    get: function () {
      return get_years_0(this.monthSpan);
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'months', {
    get: function () {
      return get_months_0(this.monthSpan);
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'weeks', {
    get: function () {
      return this.computed_0.weeks;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'daysNotIncludingWeeks', {
    get: function () {
      return this.days;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'daysIncludingWeeks', {
    get: function () {
      return this.computed_0.days + (this.computed_0.weeks * 7 | 0) | 0;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'days', {
    get: function () {
      return this.computed_0.days;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'hours', {
    get: function () {
      return this.computed_0.hours;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'minutes', {
    get: function () {
      return this.computed_0.minutes;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'seconds', {
    get: function () {
      return this.computed_0.seconds;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'milliseconds', {
    get: function () {
      return this.computed_0.milliseconds;
    }
  });
  Object.defineProperty(DateTimeSpan.prototype, 'secondsIncludingMilliseconds', {
    get: function () {
      return this.computed_0.seconds + this.computed_0.milliseconds / 1000;
    }
  });
  DateTimeSpan.prototype.compareTo_11rb$ = function (other) {
    if (this.totalMonths !== other.totalMonths)
      return this.monthSpan.compareTo_11rb$(other.monthSpan);
    return this.timeSpan.compareTo_11rb$(other.timeSpan);
  };
  DateTimeSpan.prototype.toString_6taknv$ = function (includeWeeks) {
    var $receiver = ArrayList_init();
    var tmp$, tmp$_0, tmp$_1;
    if (this.years !== 0)
      $receiver.add_11rb$(this.years.toString() + 'Y');
    if (this.months !== 0)
      $receiver.add_11rb$(this.months.toString() + 'M');
    if (includeWeeks && this.weeks !== 0)
      $receiver.add_11rb$(this.weeks.toString() + 'W');
    if (this.days !== 0 || (!includeWeeks && this.weeks !== 0))
      $receiver.add_11rb$((includeWeeks ? this.days : this.daysIncludingWeeks).toString() + 'D');
    if (this.hours !== 0)
      $receiver.add_11rb$(this.hours.toString() + 'H');
    if (this.minutes !== 0)
      $receiver.add_11rb$(this.minutes.toString() + 'm');
    if (this.seconds !== 0 || this.milliseconds !== 0.0)
      $receiver.add_11rb$(this.secondsIncludingMilliseconds.toString() + 's');
    var tmp$_2 = (tmp$ = this.monthSpan) != null ? tmp$.equals(new MonthSpan(12 * 0 | 0)) : null;
    if (tmp$_2) {
      var tmp$_3 = (tmp$_0 = this.timeSpan) != null ? tmp$_0.equals(TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble(0))) : null;
      if (!tmp$_3) {
        tmp$_3 = (tmp$_1 = this.timeSpan) != null ? tmp$_1.equals(TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble(0))) : null;
      }
      tmp$_2 = tmp$_3;
    }
    if (tmp$_2)
      $receiver.add_11rb$('0s');
    return joinToString($receiver, ' ');
  };
  DateTimeSpan.prototype.toString = function () {
    return this.toString_6taknv$(true);
  };
  function DateTimeSpan$ComputedTime(weeks, days, hours, minutes, seconds, milliseconds) {
    DateTimeSpan$ComputedTime$Companion_getInstance();
    this.weeks = weeks;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
  }
  function DateTimeSpan$ComputedTime$Companion() {
    DateTimeSpan$ComputedTime$Companion_instance = this;
  }
  DateTimeSpan$ComputedTime$Companion.prototype.invoke_fv8bff$ = function (time) {
    var $receiver = new Moduler(time.milliseconds);
    var weeks = numberToInt($receiver.double_14dthe$(numberToDouble(604800000)));
    var days = numberToInt($receiver.double_14dthe$(numberToDouble(86400000)));
    var hours = numberToInt($receiver.double_14dthe$(numberToDouble(3600000)));
    var minutes = numberToInt($receiver.double_14dthe$(numberToDouble(60000)));
    var seconds = numberToInt($receiver.double_14dthe$(numberToDouble(1000)));
    var milliseconds = $receiver.double_14dthe$(numberToDouble(1));
    return new DateTimeSpan$ComputedTime(weeks, days, hours, minutes, seconds, milliseconds);
  };
  DateTimeSpan$ComputedTime$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DateTimeSpan$ComputedTime$Companion_instance = null;
  function DateTimeSpan$ComputedTime$Companion_getInstance() {
    if (DateTimeSpan$ComputedTime$Companion_instance === null) {
      new DateTimeSpan$ComputedTime$Companion();
    }
    return DateTimeSpan$ComputedTime$Companion_instance;
  }
  DateTimeSpan$ComputedTime.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ComputedTime',
    interfaces: []
  };
  Object.defineProperty(DateTimeSpan.prototype, 'computed_0', {
    get: function () {
      return this.computed_a87s7m$_0.value;
    }
  });
  function DateTimeSpan$computed$lambda(this$DateTimeSpan) {
    return function () {
      return DateTimeSpan$ComputedTime$Companion_getInstance().invoke_fv8bff$(this$DateTimeSpan.timeSpan);
    };
  }
  DateTimeSpan.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DateTimeSpan',
    interfaces: [Comparable]
  };
  function DateTimeSpan_init(years, months, weeks, days, hours, minutes, seconds, milliseconds, $this) {
    if (years === void 0)
      years = 0;
    if (months === void 0)
      months = 0;
    if (weeks === void 0)
      weeks = 0;
    if (days === void 0)
      days = 0;
    if (hours === void 0)
      hours = 0;
    if (minutes === void 0)
      minutes = 0;
    if (seconds === void 0)
      seconds = 0;
    if (milliseconds === void 0)
      milliseconds = 0.0;
    $this = $this || Object.create(DateTimeSpan.prototype);
    DateTimeSpan.call($this, (new MonthSpan(12 * years | 0)).plus_glepj8$(new MonthSpan(months)), TimeSpan.Companion.fromWeeks_14dthe$(numberToDouble(weeks)).plus_fv8bff$(TimeSpan.Companion.fromDays_14dthe$(numberToDouble(days))).plus_fv8bff$(TimeSpan.Companion.fromHours_14dthe$(numberToDouble(hours))).plus_fv8bff$(TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble(minutes))).plus_fv8bff$(TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble(seconds))).plus_fv8bff$(TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble(milliseconds))));
    return $this;
  }
  DateTimeSpan.prototype.component1 = function () {
    return this.monthSpan;
  };
  DateTimeSpan.prototype.component2 = function () {
    return this.timeSpan;
  };
  DateTimeSpan.prototype.copy_e89ho5$ = function (monthSpan, timeSpan) {
    return new DateTimeSpan(monthSpan === void 0 ? this.monthSpan : monthSpan, timeSpan === void 0 ? this.timeSpan : timeSpan);
  };
  DateTimeSpan.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.monthSpan) | 0;
    result = result * 31 + Kotlin.hashCode(this.timeSpan) | 0;
    return result;
  };
  DateTimeSpan.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.monthSpan, other.monthSpan) && Kotlin.equals(this.timeSpan, other.timeSpan)))));
  };
  function DateTimeSpanFormat() {
  }
  DateTimeSpanFormat.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DateTimeSpanFormat',
    interfaces: []
  };
  function format_2($receiver, dd) {
    return $receiver.format_5gml0z$(dd.plus_glepj8$(new MonthSpan(0)));
  }
  function format_3($receiver, dd) {
    return $receiver.format_5gml0z$(dd.plus_fv8bff$(TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble(0))));
  }
  function parse_0($receiver, str) {
    var tmp$;
    tmp$ = $receiver.tryParse_ivxn3r$(str, true);
    if (tmp$ == null) {
      throw new DateException("Not a valid format: '" + str + "' for '" + $receiver + "'");
    }
    return tmp$;
  }
  function DateTimeTz(adjusted, offset) {
    DateTimeTz$Companion_getInstance();
    this.adjusted_0 = adjusted;
    this.offset = offset;
  }
  function DateTimeTz$Companion() {
    DateTimeTz$Companion_instance = this;
  }
  DateTimeTz$Companion.prototype.local_rq74cp$ = function (local, offset) {
    return new DateTimeTz(local, offset);
  };
  DateTimeTz$Companion.prototype.utc_rq74cp$ = function (utc, offset) {
    return new DateTimeTz(utc.plus_fv8bff$(offset.time), offset);
  };
  DateTimeTz$Companion.prototype.fromUnixLocal_s8cxhz$ = function (unix) {
    return this.fromUnixLocal_14dthe$(unix.toNumber());
  };
  DateTimeTz$Companion.prototype.fromUnixLocal_14dthe$ = function (unix) {
    return (new DateTime(unix)).localUnadjusted;
  };
  DateTimeTz$Companion.prototype.nowLocal = function () {
    return DateTime$Companion_getInstance().fromUnix_14dthe$(DateTime$Companion_getInstance().nowUnix()).localUnadjusted;
  };
  DateTimeTz$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DateTimeTz$Companion_instance = null;
  function DateTimeTz$Companion_getInstance() {
    if (DateTimeTz$Companion_instance === null) {
      new DateTimeTz$Companion();
    }
    return DateTimeTz$Companion_instance;
  }
  Object.defineProperty(DateTimeTz.prototype, 'local', {
    get: function () {
      return this.adjusted_0;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'utc', {
    get: function () {
      return this.adjusted_0.minus_fv8bff$(this.offset.time);
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'year', {
    get: function () {
      return this.adjusted_0.year;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'yearInt', {
    get: function () {
      return this.adjusted_0.yearInt;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'month', {
    get: function () {
      return this.adjusted_0.month;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'month0', {
    get: function () {
      return this.adjusted_0.month0;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'month1', {
    get: function () {
      return this.adjusted_0.month1;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'yearMonth', {
    get: function () {
      return this.adjusted_0.yearMonth;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'dayOfMonth', {
    get: function () {
      return this.adjusted_0.dayOfMonth;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'dayOfWeek', {
    get: function () {
      return this.adjusted_0.dayOfWeek;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'dayOfWeekInt', {
    get: function () {
      return this.adjusted_0.dayOfWeekInt;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'dayOfYear', {
    get: function () {
      return this.adjusted_0.dayOfYear;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'hours', {
    get: function () {
      return this.adjusted_0.hours;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'minutes', {
    get: function () {
      return this.adjusted_0.minutes;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'seconds', {
    get: function () {
      return this.adjusted_0.seconds;
    }
  });
  Object.defineProperty(DateTimeTz.prototype, 'milliseconds', {
    get: function () {
      return this.adjusted_0.milliseconds;
    }
  });
  DateTimeTz.prototype.toOffsetUnadjusted_fv8bff$ = function (offset) {
    return this.toOffsetUnadjusted_q6c6ai$(get_offset(offset));
  };
  DateTimeTz.prototype.toOffsetUnadjusted_q6c6ai$ = function (offset) {
    return DateTimeTz$Companion_getInstance().local_rq74cp$(this.local, offset);
  };
  DateTimeTz.prototype.addOffsetUnadjusted_fv8bff$ = function (offset) {
    return this.addOffsetUnadjusted_q6c6ai$(get_offset(offset));
  };
  DateTimeTz.prototype.addOffsetUnadjusted_q6c6ai$ = function (offset) {
    return DateTimeTz$Companion_getInstance().local_rq74cp$(this.local, get_offset(this.offset.time.plus_fv8bff$(offset.time)));
  };
  DateTimeTz.prototype.toOffset_fv8bff$ = function (offset) {
    return this.toOffset_q6c6ai$(get_offset(offset));
  };
  DateTimeTz.prototype.toOffset_q6c6ai$ = function (offset) {
    return DateTimeTz$Companion_getInstance().utc_rq74cp$(this.utc, offset);
  };
  DateTimeTz.prototype.addOffset_fv8bff$ = function (offset) {
    return this.addOffset_q6c6ai$(get_offset(offset));
  };
  DateTimeTz.prototype.addOffset_q6c6ai$ = function (offset) {
    return DateTimeTz$Companion_getInstance().utc_rq74cp$(this.utc, get_offset(this.offset.time.plus_fv8bff$(offset.time)));
  };
  DateTimeTz.prototype.add_e89ho5$ = function (dateSpan, timeSpan) {
    return new DateTimeTz(this.adjusted_0.add_e89ho5$(dateSpan, timeSpan), this.offset);
  };
  DateTimeTz.prototype.plus_glepj8$ = function (delta) {
    return this.add_e89ho5$(delta, TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble(0)));
  };
  DateTimeTz.prototype.plus_5gml0z$ = function (delta) {
    return this.add_e89ho5$(delta.monthSpan, delta.timeSpan);
  };
  DateTimeTz.prototype.plus_fv8bff$ = function (delta) {
    return this.add_e89ho5$(new MonthSpan(0), delta);
  };
  DateTimeTz.prototype.minus_glepj8$ = function (delta) {
    return this.plus_glepj8$(delta.unaryMinus());
  };
  DateTimeTz.prototype.minus_5gml0z$ = function (delta) {
    return this.plus_5gml0z$(delta.unaryMinus());
  };
  DateTimeTz.prototype.minus_fv8bff$ = function (delta) {
    return this.plus_fv8bff$(delta.unaryMinus());
  };
  DateTimeTz.prototype.minus_j01w8f$ = function (other) {
    var $receiver = this.utc.unixMillisDouble - other.utc.unixMillisDouble;
    return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
  };
  DateTimeTz.prototype.hashCode = function () {
    return this.local.hashCode() + this.offset.totalMinutesInt | 0;
  };
  DateTimeTz.prototype.equals = function (other) {
    return Kotlin.isType(other, DateTimeTz) && this.utc.unixMillisDouble === other.utc.unixMillisDouble;
  };
  DateTimeTz.prototype.compareTo_11rb$ = function (other) {
    return Kotlin.compareTo(this.utc.unixMillis, other.utc.unixMillis);
  };
  DateTimeTz.prototype.format_cgtbg3$ = function (format) {
    return format.format_j01w8f$(this);
  };
  DateTimeTz.prototype.format_61zpoe$ = function (format) {
    return DateFormat$Companion_getInstance().invoke_61zpoe$(format).format_j01w8f$(this);
  };
  DateTimeTz.prototype.toString_cgtbg3$ = function (format) {
    return format.format_j01w8f$(this);
  };
  DateTimeTz.prototype.toString_61zpoe$ = function (format) {
    return DateFormat$Companion_getInstance().invoke_61zpoe$(format).format_j01w8f$(this);
  };
  DateTimeTz.prototype.toString = function () {
    return DateFormat$Companion_getInstance().DEFAULT_FORMAT.format_j01w8f$(this);
  };
  DateTimeTz.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DateTimeTz',
    interfaces: [Comparable]
  };
  function DayOfWeek(name, ordinal, index0) {
    Enum.call(this);
    this.index0 = index0;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function DayOfWeek_initFields() {
    DayOfWeek_initFields = function () {
    };
    DayOfWeek$Sunday_instance = new DayOfWeek('Sunday', 0, 0);
    DayOfWeek$Monday_instance = new DayOfWeek('Monday', 1, 1);
    DayOfWeek$Tuesday_instance = new DayOfWeek('Tuesday', 2, 2);
    DayOfWeek$Wednesday_instance = new DayOfWeek('Wednesday', 3, 3);
    DayOfWeek$Thursday_instance = new DayOfWeek('Thursday', 4, 4);
    DayOfWeek$Friday_instance = new DayOfWeek('Friday', 5, 5);
    DayOfWeek$Saturday_instance = new DayOfWeek('Saturday', 6, 6);
    DayOfWeek$Companion_getInstance();
  }
  var DayOfWeek$Sunday_instance;
  function DayOfWeek$Sunday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Sunday_instance;
  }
  var DayOfWeek$Monday_instance;
  function DayOfWeek$Monday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Monday_instance;
  }
  var DayOfWeek$Tuesday_instance;
  function DayOfWeek$Tuesday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Tuesday_instance;
  }
  var DayOfWeek$Wednesday_instance;
  function DayOfWeek$Wednesday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Wednesday_instance;
  }
  var DayOfWeek$Thursday_instance;
  function DayOfWeek$Thursday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Thursday_instance;
  }
  var DayOfWeek$Friday_instance;
  function DayOfWeek$Friday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Friday_instance;
  }
  var DayOfWeek$Saturday_instance;
  function DayOfWeek$Saturday_getInstance() {
    DayOfWeek_initFields();
    return DayOfWeek$Saturday_instance;
  }
  Object.defineProperty(DayOfWeek.prototype, 'index1', {
    get: function () {
      return this.index0 + 1 | 0;
    }
  });
  Object.defineProperty(DayOfWeek.prototype, 'index0Sunday', {
    get: function () {
      return this.index0;
    }
  });
  Object.defineProperty(DayOfWeek.prototype, 'index1Sunday', {
    get: function () {
      return this.index1;
    }
  });
  Object.defineProperty(DayOfWeek.prototype, 'index0Monday', {
    get: function () {
      return umod(this.index0 - 1 | 0, 7);
    }
  });
  Object.defineProperty(DayOfWeek.prototype, 'index1Monday', {
    get: function () {
      return this.index0Monday + 1 | 0;
    }
  });
  DayOfWeek.prototype.isWeekend_kdekv2$ = function (locale) {
    if (locale === void 0)
      locale = KlockLocale$Companion_getInstance().default;
    return locale.isWeekend_76hapz$(this);
  };
  Object.defineProperty(DayOfWeek.prototype, 'localName', {
    get: function () {
      return this.localName_kdekv2$(KlockLocale$Companion_getInstance().default);
    }
  });
  DayOfWeek.prototype.localName_kdekv2$ = function (locale) {
    return locale.daysOfWeek.get_za3lpa$(this.index0);
  };
  Object.defineProperty(DayOfWeek.prototype, 'localShortName', {
    get: function () {
      return this.localShortName_kdekv2$(KlockLocale$Companion_getInstance().default);
    }
  });
  DayOfWeek.prototype.localShortName_kdekv2$ = function (locale) {
    return locale.daysOfWeekShort.get_za3lpa$(this.index0);
  };
  function DayOfWeek$Companion() {
    DayOfWeek$Companion_instance = this;
    this.Count = 7;
    this.BY_INDEX0_0 = DayOfWeek$values();
  }
  DayOfWeek$Companion.prototype.get_za3lpa$ = function (index0) {
    return this.BY_INDEX0_0[umod(index0, 7)];
  };
  DayOfWeek$Companion.prototype.firstDayOfWeek_kdekv2$ = function (locale) {
    if (locale === void 0)
      locale = KlockLocale$Companion_getInstance().default;
    return locale.firstDayOfWeek;
  };
  DayOfWeek$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DayOfWeek$Companion_instance = null;
  function DayOfWeek$Companion_getInstance() {
    DayOfWeek_initFields();
    if (DayOfWeek$Companion_instance === null) {
      new DayOfWeek$Companion();
    }
    return DayOfWeek$Companion_instance;
  }
  DayOfWeek.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DayOfWeek',
    interfaces: [Enum]
  };
  function DayOfWeek$values() {
    return [DayOfWeek$Sunday_getInstance(), DayOfWeek$Monday_getInstance(), DayOfWeek$Tuesday_getInstance(), DayOfWeek$Wednesday_getInstance(), DayOfWeek$Thursday_getInstance(), DayOfWeek$Friday_getInstance(), DayOfWeek$Saturday_getInstance()];
  }
  DayOfWeek.values = DayOfWeek$values;
  function DayOfWeek$valueOf(name) {
    switch (name) {
      case 'Sunday':
        return DayOfWeek$Sunday_getInstance();
      case 'Monday':
        return DayOfWeek$Monday_getInstance();
      case 'Tuesday':
        return DayOfWeek$Tuesday_getInstance();
      case 'Wednesday':
        return DayOfWeek$Wednesday_getInstance();
      case 'Thursday':
        return DayOfWeek$Thursday_getInstance();
      case 'Friday':
        return DayOfWeek$Friday_getInstance();
      case 'Saturday':
        return DayOfWeek$Saturday_getInstance();
      default:throwISE('No enum constant com.soywiz.klock.DayOfWeek.' + name);
    }
  }
  DayOfWeek.valueOf_61zpoe$ = DayOfWeek$valueOf;
  function ISO8601() {
    ISO8601_instance = this;
    this.DATE_CALENDAR_COMPLETE = new ISO8601$IsoDateTimeFormat('YYYYMMDD', 'YYYY-MM-DD');
    this.DATE_CALENDAR_REDUCED0 = new ISO8601$IsoDateTimeFormat(null, 'YYYY-MM');
    this.DATE_CALENDAR_REDUCED1 = new ISO8601$IsoDateTimeFormat('YYYY', null);
    this.DATE_CALENDAR_REDUCED2 = new ISO8601$IsoDateTimeFormat('YY', null);
    this.DATE_CALENDAR_EXPANDED0 = new ISO8601$IsoDateTimeFormat('\xB1YYYYYYMMDD', '\xB1YYYYYY-MM-DD');
    this.DATE_CALENDAR_EXPANDED1 = new ISO8601$IsoDateTimeFormat('\xB1YYYYYYMM', '\xB1YYYYYY-MM');
    this.DATE_CALENDAR_EXPANDED2 = new ISO8601$IsoDateTimeFormat('\xB1YYYYYY', null);
    this.DATE_CALENDAR_EXPANDED3 = new ISO8601$IsoDateTimeFormat('\xB1YYY', null);
    this.DATE_ORDINAL_COMPLETE = new ISO8601$IsoDateTimeFormat('YYYYDDD', 'YYYY-DDD');
    this.DATE_ORDINAL_EXPANDED = new ISO8601$IsoDateTimeFormat('\xB1YYYYYYDDD', '\xB1YYYYYY-DDD');
    this.DATE_WEEK_COMPLETE = new ISO8601$IsoDateTimeFormat('YYYYWwwD', 'YYYY-Www-D');
    this.DATE_WEEK_REDUCED = new ISO8601$IsoDateTimeFormat('YYYYWww', 'YYYY-Www');
    this.DATE_WEEK_EXPANDED0 = new ISO8601$IsoDateTimeFormat('\xB1YYYYYYWwwD', '\xB1YYYYYY-Www-D');
    this.DATE_WEEK_EXPANDED1 = new ISO8601$IsoDateTimeFormat('\xB1YYYYYYWww', '\xB1YYYYYY-Www');
    this.DATE_ALL_ggn2z2$_0 = lazy(ISO8601$DATE_ALL$lambda(this));
    this.TIME_LOCAL_COMPLETE = new ISO8601$IsoTimeFormat('hhmmss', 'hh:mm:ss');
    this.TIME_LOCAL_REDUCED0 = new ISO8601$IsoTimeFormat('hhmm', 'hh:mm');
    this.TIME_LOCAL_REDUCED1 = new ISO8601$IsoTimeFormat('hh', null);
    this.TIME_LOCAL_FRACTION0 = new ISO8601$IsoTimeFormat('hhmmss,ss', 'hh:mm:ss,ss');
    this.TIME_LOCAL_FRACTION1 = new ISO8601$IsoTimeFormat('hhmm,mm', 'hh:mm,mm');
    this.TIME_LOCAL_FRACTION2 = new ISO8601$IsoTimeFormat('hh,hh', null);
    this.TIME_UTC_COMPLETE = new ISO8601$IsoTimeFormat('hhmmssZ', 'hh:mm:ssZ');
    this.TIME_UTC_REDUCED0 = new ISO8601$IsoTimeFormat('hhmmZ', 'hh:mmZ');
    this.TIME_UTC_REDUCED1 = new ISO8601$IsoTimeFormat('hhZ', null);
    this.TIME_UTC_FRACTION0 = new ISO8601$IsoTimeFormat('hhmmss,ssZ', 'hh:mm:ss,ssZ');
    this.TIME_UTC_FRACTION1 = new ISO8601$IsoTimeFormat('hhmm,mmZ', 'hh:mm,mmZ');
    this.TIME_UTC_FRACTION2 = new ISO8601$IsoTimeFormat('hh,hhZ', null);
    this.TIME_RELATIVE0 = new ISO8601$IsoTimeFormat('\xB1hhmm', '\xB1hh:mm');
    this.TIME_RELATIVE1 = new ISO8601$IsoTimeFormat('\xB1hh', null);
    this.TIME_ALL_bqncst$_0 = lazy(ISO8601$TIME_ALL$lambda(this));
    this.DATETIME_COMPLETE = new ISO8601$IsoDateTimeFormat('YYYYMMDDTHHMMSS', 'YYYY-MM-DDTHH:MM:SS');
    this.INTERVAL_COMPLETE0 = new ISO8601$IsoIntervalFormat('PnnYnnMnnDTnnHnnMnnS');
    this.INTERVAL_COMPLETE1 = new ISO8601$IsoIntervalFormat('PnnYnnW');
    this.INTERVAL_REDUCED0 = new ISO8601$IsoIntervalFormat('PnnYnnMnnDTnnHnnM');
    this.INTERVAL_REDUCED1 = new ISO8601$IsoIntervalFormat('PnnYnnMnnDTnnH');
    this.INTERVAL_REDUCED2 = new ISO8601$IsoIntervalFormat('PnnYnnMnnD');
    this.INTERVAL_REDUCED3 = new ISO8601$IsoIntervalFormat('PnnYnnM');
    this.INTERVAL_REDUCED4 = new ISO8601$IsoIntervalFormat('PnnY');
    this.INTERVAL_DECIMAL0 = new ISO8601$IsoIntervalFormat('PnnYnnMnnDTnnHnnMnn,nnS');
    this.INTERVAL_DECIMAL1 = new ISO8601$IsoIntervalFormat('PnnYnnMnnDTnnHnn,nnM');
    this.INTERVAL_DECIMAL2 = new ISO8601$IsoIntervalFormat('PnnYnnMnnDTnn,nnH');
    this.INTERVAL_DECIMAL3 = new ISO8601$IsoIntervalFormat('PnnYnnMnn,nnD');
    this.INTERVAL_DECIMAL4 = new ISO8601$IsoIntervalFormat('PnnYnn,nnM');
    this.INTERVAL_DECIMAL5 = new ISO8601$IsoIntervalFormat('PnnYnn,nnW');
    this.INTERVAL_DECIMAL6 = new ISO8601$IsoIntervalFormat('PnnY');
    this.INTERVAL_ZERO_OMIT0 = new ISO8601$IsoIntervalFormat('PnnYnnDTnnHnnMnnS');
    this.INTERVAL_ZERO_OMIT1 = new ISO8601$IsoIntervalFormat('PnnYnnDTnnHnnM');
    this.INTERVAL_ZERO_OMIT2 = new ISO8601$IsoIntervalFormat('PnnYnnDTnnH');
    this.INTERVAL_ZERO_OMIT3 = new ISO8601$IsoIntervalFormat('PnnYnnD');
    this.INTERVAL_ALL_c087t1$_0 = lazy(ISO8601$INTERVAL_ALL$lambda(this));
    this.DATE = new ISO8601$DATE$ObjectLiteral();
    this.TIME = new ISO8601$TIME$ObjectLiteral();
    this.INTERVAL = new ISO8601$INTERVAL$ObjectLiteral();
  }
  function ISO8601$BaseIsoTimeFormat(format) {
    ISO8601$BaseIsoTimeFormat$Companion_getInstance();
    this.format = format;
    this.dateTimeFormat_0 = new ISO8601$BaseIsoDateTimeFormat(this.format);
  }
  function ISO8601$BaseIsoTimeFormat$Companion() {
    ISO8601$BaseIsoTimeFormat$Companion_instance = this;
    this.ref_0 = DateTime$Companion_getInstance().invoke_ui44o2$(1900, 1, 1);
  }
  ISO8601$BaseIsoTimeFormat$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ISO8601$BaseIsoTimeFormat$Companion_instance = null;
  function ISO8601$BaseIsoTimeFormat$Companion_getInstance() {
    if (ISO8601$BaseIsoTimeFormat$Companion_instance === null) {
      new ISO8601$BaseIsoTimeFormat$Companion();
    }
    return ISO8601$BaseIsoTimeFormat$Companion_instance;
  }
  ISO8601$BaseIsoTimeFormat.prototype.format_fv8bff$ = function (dd) {
    return format_1(this.dateTimeFormat_0, ISO8601$BaseIsoTimeFormat$Companion_getInstance().ref_0.plus_fv8bff$(dd));
  };
  ISO8601$BaseIsoTimeFormat.prototype.tryParse_ivxn3r$ = function (str, doThrow) {
    var tmp$;
    return (tmp$ = this.dateTimeFormat_0.tryParse_ivxn3r$(str, doThrow)) != null ? tmp$.utc.minus_mw5vjr$(ISO8601$BaseIsoTimeFormat$Companion_getInstance().ref_0) : null;
  };
  ISO8601$BaseIsoTimeFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BaseIsoTimeFormat',
    interfaces: [TimeFormat]
  };
  ISO8601$BaseIsoTimeFormat.prototype.component1 = function () {
    return this.format;
  };
  ISO8601$BaseIsoTimeFormat.prototype.copy_61zpoe$ = function (format) {
    return new ISO8601$BaseIsoTimeFormat(format === void 0 ? this.format : format);
  };
  ISO8601$BaseIsoTimeFormat.prototype.toString = function () {
    return 'BaseIsoTimeFormat(format=' + Kotlin.toString(this.format) + ')';
  };
  ISO8601$BaseIsoTimeFormat.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.format) | 0;
    return result;
  };
  ISO8601$BaseIsoTimeFormat.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.format, other.format))));
  };
  function ISO8601$BaseIsoDateTimeFormat(format, twoDigitBaseYear) {
    if (twoDigitBaseYear === void 0)
      twoDigitBaseYear = 1900;
    this.format = format;
    this.twoDigitBaseYear = twoDigitBaseYear;
  }
  ISO8601$BaseIsoDateTimeFormat.prototype.format_j01w8f$ = function (dd) {
    var $receiver = StringBuilder_init();
    var isUtc = endsWith(this.format, 90);
    var d = isUtc ? dd.utc : dd.local;
    var s = d.copyDayOfMonth_4lrum3$(void 0, void 0, void 0, 0, 0, 0, 0);
    var time = d.minus_mw5vjr$(s);
    var fmtReader = new MicroStrReader(this.format);
    while (fmtReader.hasMore) {
      if (fmtReader.tryRead_61zpoe$('YYYYYY')) {
        $receiver.append_gw00v9$(padded(abs(d.yearInt), 6));
      }
       else if (fmtReader.tryRead_61zpoe$('YYYY')) {
        $receiver.append_gw00v9$(padded(abs(d.yearInt), 4));
      }
       else if (fmtReader.tryRead_61zpoe$('YY')) {
        $receiver.append_gw00v9$(padded(abs(d.yearInt) % 100, 2));
      }
       else if (fmtReader.tryRead_61zpoe$('MM'))
        $receiver.append_gw00v9$(padded(d.month1, 2));
      else if (fmtReader.tryRead_61zpoe$('DD'))
        $receiver.append_gw00v9$(padded(d.dayOfMonth, 2));
      else if (fmtReader.tryRead_61zpoe$('DDD'))
        $receiver.append_gw00v9$(padded(d.dayOfWeekInt, 3));
      else if (fmtReader.tryRead_61zpoe$('ww'))
        $receiver.append_gw00v9$(padded(get_weekOfYear1(d), 2));
      else if (fmtReader.tryRead_61zpoe$('D'))
        $receiver.append_s8jyv4$(d.dayOfWeek.index1Monday);
      else if (fmtReader.tryRead_61zpoe$('hh,hh'))
        $receiver.append_gw00v9$(replace(padded_0(time.hours, 2, 2), 46, 44));
      else if (fmtReader.tryRead_61zpoe$('hh'))
        $receiver.append_gw00v9$(padded(d.hours, 2));
      else if (fmtReader.tryRead_61zpoe$('mm,mm'))
        $receiver.append_gw00v9$(replace(padded_0(time.minutes % 60.0, 2, 2), 46, 44));
      else if (fmtReader.tryRead_61zpoe$('mm'))
        $receiver.append_gw00v9$(padded(d.minutes, 2));
      else if (fmtReader.tryRead_61zpoe$('ss,ss'))
        $receiver.append_gw00v9$(replace(padded_0(time.seconds, 2, 2), 46, 44));
      else if (fmtReader.tryRead_61zpoe$('ss'))
        $receiver.append_gw00v9$(padded(d.seconds, 2));
      else if (fmtReader.tryRead_61zpoe$('\xB1'))
        $receiver.append_gw00v9$(d.yearInt < 0 ? '-' : '+');
      else
        $receiver.append_s8itvh$(unboxChar(fmtReader.readChar()));
    }
    return $receiver.toString();
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.tryParse_ivxn3r$$default = function (str, doThrow) {
    var $receiver = this.tryParse_0(str);
    if (doThrow && $receiver == null)
      throw new DateException("Can't parse " + str + ' with ' + this.format);
    return $receiver;
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.tryParse_0 = function (str) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5, tmp$_6, tmp$_7, tmp$_8, tmp$_9, tmp$_10, tmp$_11, tmp$_12, tmp$_13;
    var isUtc = false;
    var sign = 1;
    var year = this.twoDigitBaseYear;
    var month = 1;
    var dayOfMonth = 1;
    var dayOfWeek = -1;
    var dayOfYear = -1;
    var weekOfYear = -1;
    var hours = 0.0;
    var minutes = 0.0;
    var seconds = 0.0;
    var reader = new MicroStrReader(str);
    var fmtReader = new MicroStrReader(this.format);
    while (fmtReader.hasMore) {
      if (fmtReader.tryRead_61zpoe$('Z'))
        isUtc = true;
      else if (fmtReader.tryRead_61zpoe$('YYYYYY')) {
        tmp$ = reader.tryReadInt_za3lpa$(6);
        if (tmp$ == null) {
          return null;
        }
        year = tmp$;
      }
       else if (fmtReader.tryRead_61zpoe$('YYYY')) {
        tmp$_0 = reader.tryReadInt_za3lpa$(4);
        if (tmp$_0 == null) {
          return null;
        }
        year = tmp$_0;
      }
       else if (fmtReader.tryRead_61zpoe$('YY')) {
        tmp$_1 = reader.tryReadInt_za3lpa$(2);
        if (tmp$_1 == null) {
          return null;
        }
        var base = tmp$_1;
        year = this.twoDigitBaseYear + base | 0;
      }
       else if (fmtReader.tryRead_61zpoe$('MM')) {
        tmp$_2 = reader.tryReadInt_za3lpa$(2);
        if (tmp$_2 == null) {
          return null;
        }
        month = tmp$_2;
      }
       else if (fmtReader.tryRead_61zpoe$('DD')) {
        tmp$_3 = reader.tryReadInt_za3lpa$(4);
        if (tmp$_3 == null) {
          return null;
        }
        dayOfMonth = tmp$_3;
      }
       else if (fmtReader.tryRead_61zpoe$('DDD')) {
        tmp$_4 = reader.tryReadInt_za3lpa$(3);
        if (tmp$_4 == null) {
          return null;
        }
        dayOfYear = tmp$_4;
      }
       else if (fmtReader.tryRead_61zpoe$('ww')) {
        tmp$_5 = reader.tryReadInt_za3lpa$(2);
        if (tmp$_5 == null) {
          return null;
        }
        weekOfYear = tmp$_5;
      }
       else if (fmtReader.tryRead_61zpoe$('D')) {
        tmp$_6 = reader.tryReadInt_za3lpa$(1);
        if (tmp$_6 == null) {
          return null;
        }
        dayOfWeek = tmp$_6;
      }
       else if (fmtReader.tryRead_61zpoe$('hh,hh')) {
        tmp$_7 = reader.tryReadDouble_za3lpa$(5);
        if (tmp$_7 == null) {
          return null;
        }
        hours = tmp$_7;
      }
       else if (fmtReader.tryRead_61zpoe$('hh')) {
        tmp$_8 = reader.tryReadDouble_za3lpa$(2);
        if (tmp$_8 == null) {
          return null;
        }
        hours = tmp$_8;
      }
       else if (fmtReader.tryRead_61zpoe$('mm,mm')) {
        tmp$_9 = reader.tryReadDouble_za3lpa$(5);
        if (tmp$_9 == null) {
          return null;
        }
        minutes = tmp$_9;
      }
       else if (fmtReader.tryRead_61zpoe$('mm')) {
        tmp$_10 = reader.tryReadDouble_za3lpa$(2);
        if (tmp$_10 == null) {
          return null;
        }
        minutes = tmp$_10;
      }
       else if (fmtReader.tryRead_61zpoe$('ss,ss')) {
        tmp$_11 = reader.tryReadDouble_za3lpa$(5);
        if (tmp$_11 == null) {
          return null;
        }
        seconds = tmp$_11;
      }
       else if (fmtReader.tryRead_61zpoe$('ss')) {
        tmp$_12 = reader.tryReadDouble_za3lpa$(2);
        if (tmp$_12 == null) {
          return null;
        }
        seconds = tmp$_12;
      }
       else if (fmtReader.tryRead_61zpoe$('\xB1')) {
        switch (unboxChar(reader.readChar())) {
          case 43:
            sign = 1;
            break;
          case 45:
            sign = -1;
            break;
          default:return null;
        }
      }
       else if (unboxChar(fmtReader.readChar()) !== unboxChar(reader.readChar()))
        return null;
    }
    if (reader.hasMore)
      return null;
    if (dayOfYear >= 0) {
      var tmp$_14 = DateTime$Companion_getInstance().invoke_ui44o2$(year, 1, 1);
      var $receiver = dayOfYear - 1 | 0;
      tmp$_13 = tmp$_14.plus_fv8bff$(TimeSpan.Companion.fromDays_14dthe$(numberToDouble($receiver)));
    }
     else if (weekOfYear >= 0) {
      var reference = first_0(new Year(year), DayOfWeek$Thursday_getInstance()).minus_fv8bff$(TimeSpan.Companion.fromDays_14dthe$(numberToDouble(3)));
      var days = ((weekOfYear - 1 | 0) * 7 | 0) + (dayOfWeek - 1) | 0;
      tmp$_13 = reference.plus_fv8bff$(TimeSpan.Companion.fromDays_14dthe$(numberToDouble(days)));
    }
     else
      tmp$_13 = DateTime$Companion_getInstance().invoke_ui44o2$(year, month, dayOfMonth);
    var dateTime = tmp$_13;
    var $receiver_0 = hours;
    var tmp$_15 = dateTime.plus_fv8bff$(TimeSpan.Companion.fromHours_14dthe$(numberToDouble($receiver_0)));
    var $receiver_1 = minutes;
    var tmp$_16 = tmp$_15.plus_fv8bff$(TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble($receiver_1)));
    var $receiver_2 = seconds;
    return tmp$_16.plus_fv8bff$(TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble($receiver_2))).local;
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.withTwoDigitBaseYear_za3lpa$ = function (twoDigitBaseYear) {
    if (twoDigitBaseYear === void 0)
      twoDigitBaseYear = 1900;
    return new ISO8601$BaseIsoDateTimeFormat(this.format, twoDigitBaseYear);
  };
  ISO8601$BaseIsoDateTimeFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BaseIsoDateTimeFormat',
    interfaces: [DateFormat]
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.component1 = function () {
    return this.format;
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.component2 = function () {
    return this.twoDigitBaseYear;
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.copy_bm4lxs$ = function (format, twoDigitBaseYear) {
    return new ISO8601$BaseIsoDateTimeFormat(format === void 0 ? this.format : format, twoDigitBaseYear === void 0 ? this.twoDigitBaseYear : twoDigitBaseYear);
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.toString = function () {
    return 'BaseIsoDateTimeFormat(format=' + Kotlin.toString(this.format) + (', twoDigitBaseYear=' + Kotlin.toString(this.twoDigitBaseYear)) + ')';
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.format) | 0;
    result = result * 31 + Kotlin.hashCode(this.twoDigitBaseYear) | 0;
    return result;
  };
  ISO8601$BaseIsoDateTimeFormat.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.format, other.format) && Kotlin.equals(this.twoDigitBaseYear, other.twoDigitBaseYear)))));
  };
  function ISO8601$IsoIntervalFormat(format) {
    this.format = format;
  }
  ISO8601$IsoIntervalFormat.prototype.format_5gml0z$ = function (dd) {
    var $receiver = StringBuilder_init();
    var fmtReader = new MicroStrReader(this.format);
    var time = {v: false};
    while (fmtReader.hasMore) {
      if (fmtReader.tryRead_61zpoe$('T')) {
        $receiver.append_s8itvh$(84);
        time.v = true;
      }
       else if (fmtReader.tryRead_61zpoe$('nnY'))
        $receiver.append_s8jyv4$(dd.years).append_s8itvh$(89);
      else if (fmtReader.tryRead_61zpoe$('nnM'))
        $receiver.append_s8jyv4$(time.v ? dd.minutes : dd.months).append_s8itvh$(77);
      else if (fmtReader.tryRead_61zpoe$('nnD'))
        $receiver.append_s8jyv4$(dd.daysIncludingWeeks).append_s8itvh$(68);
      else if (fmtReader.tryRead_61zpoe$('nnH'))
        $receiver.append_s8jyv4$(dd.hours).append_s8itvh$(72);
      else if (fmtReader.tryRead_61zpoe$('nnS'))
        $receiver.append_s8jyv4$(dd.seconds).append_s8itvh$(83);
      else
        $receiver.append_s8itvh$(unboxChar(fmtReader.readChar()));
    }
    return $receiver.toString();
  };
  ISO8601$IsoIntervalFormat.prototype.tryParse_ivxn3r$ = function (str, doThrow) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    var time = false;
    var years = 0.0;
    var months = 0.0;
    var days = 0.0;
    var hours = 0.0;
    var minutes = 0.0;
    var seconds = 0.0;
    var reader = new MicroStrReader(str);
    var fmtReader = new MicroStrReader(this.format);
    while (fmtReader.hasMore) {
      if (fmtReader.tryRead_61zpoe$('nn,nnY') || fmtReader.tryRead_61zpoe$('nnY')) {
        tmp$ = reader.tryReadDouble();
        if (tmp$ == null) {
          return null;
        }
        years = tmp$;
        if (!reader.tryRead_61zpoe$('Y'))
          return null;
      }
       else if (fmtReader.tryRead_61zpoe$('nn,nnM') || fmtReader.tryRead_61zpoe$('nnM')) {
        if (time) {
          tmp$_0 = reader.tryReadDouble();
          if (tmp$_0 == null) {
            return null;
          }
          minutes = tmp$_0;
        }
         else {
          tmp$_1 = reader.tryReadDouble();
          if (tmp$_1 == null) {
            return null;
          }
          months = tmp$_1;
        }
        if (!reader.tryRead_61zpoe$('M'))
          return null;
      }
       else if (fmtReader.tryRead_61zpoe$('nn,nnD') || fmtReader.tryRead_61zpoe$('nnD')) {
        tmp$_2 = reader.tryReadDouble();
        if (tmp$_2 == null) {
          return null;
        }
        days = tmp$_2;
        if (!reader.tryRead_61zpoe$('D'))
          return null;
      }
       else if (fmtReader.tryRead_61zpoe$('nn,nnH') || fmtReader.tryRead_61zpoe$('nnH')) {
        tmp$_3 = reader.tryReadDouble();
        if (tmp$_3 == null) {
          return null;
        }
        hours = tmp$_3;
        if (!reader.tryRead_61zpoe$('H'))
          return null;
      }
       else if (fmtReader.tryRead_61zpoe$('nn,nnS') || fmtReader.tryRead_61zpoe$('nnS')) {
        tmp$_4 = reader.tryReadDouble();
        if (tmp$_4 == null) {
          return null;
        }
        seconds = tmp$_4;
        if (!reader.tryRead_61zpoe$('S'))
          return null;
      }
       else {
        var char = unboxChar(fmtReader.readChar());
        if (char !== unboxChar(reader.readChar()))
          return null;
        if (char === 84)
          time = true;
      }
    }
    var tmp$_5 = new MonthSpan(numberToInt(years * 12 + months));
    var $receiver = days;
    var tmp$_6 = TimeSpan.Companion.fromDays_14dthe$(numberToDouble($receiver));
    var $receiver_0 = hours;
    var tmp$_7 = tmp$_6.plus_fv8bff$(TimeSpan.Companion.fromHours_14dthe$(numberToDouble($receiver_0)));
    var $receiver_1 = minutes;
    var tmp$_8 = tmp$_7.plus_fv8bff$(TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble($receiver_1)));
    var $receiver_2 = seconds;
    return tmp$_5.plus_fv8bff$(tmp$_8.plus_fv8bff$(TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble($receiver_2))));
  };
  ISO8601$IsoIntervalFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IsoIntervalFormat',
    interfaces: [DateTimeSpanFormat]
  };
  function ISO8601$IsoTimeFormat(basicFormat, extendedFormat) {
    this.basicFormat = basicFormat;
    this.extendedFormat = extendedFormat;
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var tmp$_3;
    if ((tmp$_0 = (tmp$ = this.basicFormat) != null ? tmp$ : this.extendedFormat) != null)
      tmp$_3 = tmp$_0;
    else {
      throw new NotImplementedError_init();
    }
    this.basic = new ISO8601$BaseIsoTimeFormat(tmp$_3);
    var tmp$_4;
    if ((tmp$_2 = (tmp$_1 = this.extendedFormat) != null ? tmp$_1 : this.basicFormat) != null)
      tmp$_4 = tmp$_2;
    else {
      throw new NotImplementedError_init();
    }
    this.extended = new ISO8601$BaseIsoTimeFormat(tmp$_4);
  }
  ISO8601$IsoTimeFormat.prototype.format_fv8bff$ = function (dd) {
    return this.extended.format_fv8bff$(dd);
  };
  ISO8601$IsoTimeFormat.prototype.tryParse_ivxn3r$ = function (str, doThrow) {
    var tmp$, tmp$_0, tmp$_1;
    tmp$_1 = (tmp$ = this.basic.tryParse_ivxn3r$(str, false)) != null ? tmp$ : this.extended.tryParse_ivxn3r$(str, false);
    if (tmp$_1 == null) {
      if (doThrow)
        throw new DateException('Invalid format ' + str);
      else
        tmp$_0 = null;
      tmp$_1 = tmp$_0;
    }
    return tmp$_1;
  };
  ISO8601$IsoTimeFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IsoTimeFormat',
    interfaces: [TimeFormat]
  };
  ISO8601$IsoTimeFormat.prototype.component1 = function () {
    return this.basicFormat;
  };
  ISO8601$IsoTimeFormat.prototype.component2 = function () {
    return this.extendedFormat;
  };
  ISO8601$IsoTimeFormat.prototype.copy_rkkr90$ = function (basicFormat, extendedFormat) {
    return new ISO8601$IsoTimeFormat(basicFormat === void 0 ? this.basicFormat : basicFormat, extendedFormat === void 0 ? this.extendedFormat : extendedFormat);
  };
  ISO8601$IsoTimeFormat.prototype.toString = function () {
    return 'IsoTimeFormat(basicFormat=' + Kotlin.toString(this.basicFormat) + (', extendedFormat=' + Kotlin.toString(this.extendedFormat)) + ')';
  };
  ISO8601$IsoTimeFormat.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.basicFormat) | 0;
    result = result * 31 + Kotlin.hashCode(this.extendedFormat) | 0;
    return result;
  };
  ISO8601$IsoTimeFormat.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.basicFormat, other.basicFormat) && Kotlin.equals(this.extendedFormat, other.extendedFormat)))));
  };
  function ISO8601$IsoDateTimeFormat(basicFormat, extendedFormat) {
    this.basicFormat = basicFormat;
    this.extendedFormat = extendedFormat;
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var tmp$_3;
    if ((tmp$_0 = (tmp$ = this.basicFormat) != null ? tmp$ : this.extendedFormat) != null)
      tmp$_3 = tmp$_0;
    else {
      throw new NotImplementedError_init();
    }
    this.basic = new ISO8601$BaseIsoDateTimeFormat(tmp$_3);
    var tmp$_4;
    if ((tmp$_2 = (tmp$_1 = this.extendedFormat) != null ? tmp$_1 : this.basicFormat) != null)
      tmp$_4 = tmp$_2;
    else {
      throw new NotImplementedError_init();
    }
    this.extended = new ISO8601$BaseIsoDateTimeFormat(tmp$_4);
  }
  ISO8601$IsoDateTimeFormat.prototype.format_j01w8f$ = function (dd) {
    return this.extended.format_j01w8f$(dd);
  };
  ISO8601$IsoDateTimeFormat.prototype.tryParse_ivxn3r$$default = function (str, doThrow) {
    var tmp$, tmp$_0, tmp$_1;
    tmp$_1 = (tmp$ = this.basic.tryParse_ivxn3r$(str, false)) != null ? tmp$ : this.extended.tryParse_ivxn3r$(str, false);
    if (tmp$_1 == null) {
      if (doThrow)
        throw new DateException('Invalid format ' + str);
      else
        tmp$_0 = null;
      tmp$_1 = tmp$_0;
    }
    return tmp$_1;
  };
  ISO8601$IsoDateTimeFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IsoDateTimeFormat',
    interfaces: [DateFormat]
  };
  ISO8601$IsoDateTimeFormat.prototype.component1 = function () {
    return this.basicFormat;
  };
  ISO8601$IsoDateTimeFormat.prototype.component2 = function () {
    return this.extendedFormat;
  };
  ISO8601$IsoDateTimeFormat.prototype.copy_rkkr90$ = function (basicFormat, extendedFormat) {
    return new ISO8601$IsoDateTimeFormat(basicFormat === void 0 ? this.basicFormat : basicFormat, extendedFormat === void 0 ? this.extendedFormat : extendedFormat);
  };
  ISO8601$IsoDateTimeFormat.prototype.toString = function () {
    return 'IsoDateTimeFormat(basicFormat=' + Kotlin.toString(this.basicFormat) + (', extendedFormat=' + Kotlin.toString(this.extendedFormat)) + ')';
  };
  ISO8601$IsoDateTimeFormat.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.basicFormat) | 0;
    result = result * 31 + Kotlin.hashCode(this.extendedFormat) | 0;
    return result;
  };
  ISO8601$IsoDateTimeFormat.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.basicFormat, other.basicFormat) && Kotlin.equals(this.extendedFormat, other.extendedFormat)))));
  };
  Object.defineProperty(ISO8601.prototype, 'DATE_ALL', {
    get: function () {
      return this.DATE_ALL_ggn2z2$_0.value;
    }
  });
  Object.defineProperty(ISO8601.prototype, 'TIME_ALL', {
    get: function () {
      return this.TIME_ALL_bqncst$_0.value;
    }
  });
  Object.defineProperty(ISO8601.prototype, 'INTERVAL_ALL', {
    get: function () {
      return this.INTERVAL_ALL_c087t1$_0.value;
    }
  });
  function ISO8601$DATE_ALL$lambda(this$ISO8601) {
    return function () {
      return listOf([this$ISO8601.DATE_CALENDAR_COMPLETE, this$ISO8601.DATE_CALENDAR_REDUCED0, this$ISO8601.DATE_CALENDAR_REDUCED1, this$ISO8601.DATE_CALENDAR_REDUCED2, this$ISO8601.DATE_CALENDAR_EXPANDED0, this$ISO8601.DATE_CALENDAR_EXPANDED1, this$ISO8601.DATE_CALENDAR_EXPANDED2, this$ISO8601.DATE_CALENDAR_EXPANDED3, this$ISO8601.DATE_ORDINAL_COMPLETE, this$ISO8601.DATE_ORDINAL_EXPANDED, this$ISO8601.DATE_WEEK_COMPLETE, this$ISO8601.DATE_WEEK_REDUCED, this$ISO8601.DATE_WEEK_EXPANDED0, this$ISO8601.DATE_WEEK_EXPANDED1]);
    };
  }
  function ISO8601$TIME_ALL$lambda(this$ISO8601) {
    return function () {
      return listOf([this$ISO8601.TIME_LOCAL_COMPLETE, this$ISO8601.TIME_LOCAL_REDUCED0, this$ISO8601.TIME_LOCAL_REDUCED1, this$ISO8601.TIME_LOCAL_FRACTION0, this$ISO8601.TIME_LOCAL_FRACTION1, this$ISO8601.TIME_LOCAL_FRACTION2, this$ISO8601.TIME_UTC_COMPLETE, this$ISO8601.TIME_UTC_REDUCED0, this$ISO8601.TIME_UTC_REDUCED1, this$ISO8601.TIME_UTC_FRACTION0, this$ISO8601.TIME_UTC_FRACTION1, this$ISO8601.TIME_UTC_FRACTION2, this$ISO8601.TIME_RELATIVE0, this$ISO8601.TIME_RELATIVE1]);
    };
  }
  function ISO8601$INTERVAL_ALL$lambda(this$ISO8601) {
    return function () {
      return listOf([this$ISO8601.INTERVAL_COMPLETE0, this$ISO8601.INTERVAL_COMPLETE1, this$ISO8601.INTERVAL_REDUCED0, this$ISO8601.INTERVAL_REDUCED1, this$ISO8601.INTERVAL_REDUCED2, this$ISO8601.INTERVAL_REDUCED3, this$ISO8601.INTERVAL_REDUCED4, this$ISO8601.INTERVAL_DECIMAL0, this$ISO8601.INTERVAL_DECIMAL1, this$ISO8601.INTERVAL_DECIMAL2, this$ISO8601.INTERVAL_DECIMAL3, this$ISO8601.INTERVAL_DECIMAL4, this$ISO8601.INTERVAL_DECIMAL5, this$ISO8601.INTERVAL_DECIMAL6, this$ISO8601.INTERVAL_ZERO_OMIT0, this$ISO8601.INTERVAL_ZERO_OMIT1, this$ISO8601.INTERVAL_ZERO_OMIT2, this$ISO8601.INTERVAL_ZERO_OMIT3]);
    };
  }
  function ISO8601$DATE$ObjectLiteral() {
  }
  ISO8601$DATE$ObjectLiteral.prototype.format_j01w8f$ = function (dd) {
    return ISO8601_getInstance().DATE_CALENDAR_COMPLETE.format_j01w8f$(dd);
  };
  ISO8601$DATE$ObjectLiteral.prototype.tryParse_ivxn3r$$default = function (str, doThrow) {
    var tmp$;
    var $receiver = ISO8601_getInstance().DATE_ALL;
    var tmp$_0;
    var n = 0;
    while (n < $receiver.size) {
      var result = $receiver.get_za3lpa$((tmp$_0 = n, n = tmp$_0 + 1 | 0, tmp$_0)).extended.tryParse_ivxn3r$(str, false);
      if (result != null)
        return result;
    }
    var $receiver_0 = ISO8601_getInstance().DATE_ALL;
    var tmp$_1;
    var n_0 = 0;
    while (n_0 < $receiver_0.size) {
      var result_0 = $receiver_0.get_za3lpa$((tmp$_1 = n_0, n_0 = tmp$_1 + 1 | 0, tmp$_1)).basic.tryParse_ivxn3r$(str, false);
      if (result_0 != null)
        return result_0;
    }
    if (doThrow)
      throw new DateException('Invalid format');
    else
      tmp$ = null;
    return tmp$;
  };
  ISO8601$DATE$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [DateFormat]
  };
  function ISO8601$TIME$ObjectLiteral() {
  }
  ISO8601$TIME$ObjectLiteral.prototype.format_fv8bff$ = function (dd) {
    return ISO8601_getInstance().TIME_LOCAL_FRACTION0.format_fv8bff$(dd);
  };
  ISO8601$TIME$ObjectLiteral.prototype.tryParse_ivxn3r$ = function (str, doThrow) {
    var tmp$;
    var $receiver = ISO8601_getInstance().TIME_ALL;
    var tmp$_0;
    var n = 0;
    while (n < $receiver.size) {
      var result = $receiver.get_za3lpa$((tmp$_0 = n, n = tmp$_0 + 1 | 0, tmp$_0)).extended.tryParse_ivxn3r$(str, false);
      if (result != null)
        return result;
    }
    var $receiver_0 = ISO8601_getInstance().TIME_ALL;
    var tmp$_1;
    var n_0 = 0;
    while (n_0 < $receiver_0.size) {
      var result_0 = $receiver_0.get_za3lpa$((tmp$_1 = n_0, n_0 = tmp$_1 + 1 | 0, tmp$_1)).basic.tryParse_ivxn3r$(str, false);
      if (result_0 != null)
        return result_0;
    }
    if (doThrow)
      throw new DateException('Invalid format');
    else
      tmp$ = null;
    return tmp$;
  };
  ISO8601$TIME$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [TimeFormat]
  };
  function ISO8601$INTERVAL$ObjectLiteral() {
  }
  ISO8601$INTERVAL$ObjectLiteral.prototype.format_5gml0z$ = function (dd) {
    return ISO8601_getInstance().INTERVAL_DECIMAL0.format_5gml0z$(dd);
  };
  ISO8601$INTERVAL$ObjectLiteral.prototype.tryParse_ivxn3r$ = function (str, doThrow) {
    var tmp$;
    var $receiver = ISO8601_getInstance().INTERVAL_ALL;
    var tmp$_0;
    var n = 0;
    while (n < $receiver.size) {
      var result = $receiver.get_za3lpa$((tmp$_0 = n, n = tmp$_0 + 1 | 0, tmp$_0)).tryParse_ivxn3r$(str, false);
      if (result != null)
        return result;
    }
    if (doThrow)
      throw new DateException('Invalid format');
    else
      tmp$ = null;
    return tmp$;
  };
  ISO8601$INTERVAL$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [DateTimeSpanFormat]
  };
  ISO8601.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ISO8601',
    interfaces: []
  };
  var ISO8601_instance = null;
  function ISO8601_getInstance() {
    if (ISO8601_instance === null) {
      new ISO8601();
    }
    return ISO8601_instance;
  }
  function first_0($receiver, dayOfWeek) {
    var start = DateTime$Companion_getInstance().invoke_ui44o2$($receiver.year, 1, 1);
    var n = 0;
    while (true) {
      var $receiver_0 = n;
      var time = start.plus_fv8bff$(TimeSpan.Companion.fromDays_14dthe$(numberToDouble($receiver_0)));
      if (time.dayOfWeek === dayOfWeek)
        return time;
      n = n + 1 | 0;
    }
  }
  function get_weekOfYear0($receiver) {
    var firstThursday = first_0($receiver.year, DayOfWeek$Thursday_getInstance());
    var offset = firstThursday.dayOfMonth - 3 | 0;
    return ($receiver.dayOfYear - offset | 0) / 7 | 0;
  }
  function get_weekOfYear1($receiver) {
    return get_weekOfYear0($receiver) + 1 | 0;
  }
  function get_weekOfYear0_0($receiver) {
    return get_weekOfYear0($receiver.local);
  }
  function get_weekOfYear1_0($receiver) {
    return get_weekOfYear1($receiver.local);
  }
  var KlockLocale_default;
  function KlockLocale() {
    KlockLocale$Companion_getInstance();
    this.monthsShort_5mhx25$_0 = lazy(KlockLocale$monthsShort$lambda(this));
    this.daysOfWeekShort_s4ah9w$_0 = lazy(KlockLocale$daysOfWeekShort$lambda(this));
    this.h12Marker_ihl9tv$_0 = listOf(['AM', 'OM']);
    this.formatDateTimeMedium_5qrovt$_0 = this.format_61zpoe$('MMM d, y h:mm:ss a');
    this.formatDateTimeShort_7afaxo$_0 = this.format_61zpoe$('M/d/yy h:mm a');
    this.formatDateFull_zhfveu$_0 = this.format_61zpoe$('EEEE, MMMM d, y');
    this.formatDateLong_ze8wer$_0 = this.format_61zpoe$('MMMM d, y');
    this.formatDateMedium_i9vt5g$_0 = this.format_61zpoe$('MMM d, y');
    this.formatDateShort_slhdw1$_0 = this.format_61zpoe$('M/d/yy');
    this.formatTimeMedium_a5dzkl$_0 = this.format_61zpoe$('HH:mm:ss');
    this.formatTimeShort_o9xygi$_0 = this.format_61zpoe$('HH:mm');
  }
  Object.defineProperty(KlockLocale.prototype, 'monthsShort', {
    get: function () {
      return this.monthsShort_5mhx25$_0.value;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'daysOfWeekShort', {
    get: function () {
      return this.daysOfWeekShort_s4ah9w$_0.value;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'months3', {
    get: function () {
      return this.monthsShort;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'h12Marker', {
    get: function () {
      return this.h12Marker_ihl9tv$_0;
    }
  });
  KlockLocale.prototype.intToString_za3lpa$ = function (value) {
    return value.toString();
  };
  KlockLocale.prototype.isWeekend_76hapz$ = function (dayOfWeek) {
    return dayOfWeek === DayOfWeek$Saturday_getInstance() || dayOfWeek === DayOfWeek$Sunday_getInstance();
  };
  KlockLocale.prototype.format_61zpoe$ = function (str) {
    return new PatternDateFormat(str, this);
  };
  Object.defineProperty(KlockLocale.prototype, 'formatDateTimeMedium', {
    get: function () {
      return this.formatDateTimeMedium_5qrovt$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatDateTimeShort', {
    get: function () {
      return this.formatDateTimeShort_7afaxo$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatDateFull', {
    get: function () {
      return this.formatDateFull_zhfveu$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatDateLong', {
    get: function () {
      return this.formatDateLong_ze8wer$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatDateMedium', {
    get: function () {
      return this.formatDateMedium_i9vt5g$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatDateShort', {
    get: function () {
      return this.formatDateShort_slhdw1$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatTimeMedium', {
    get: function () {
      return this.formatTimeMedium_a5dzkl$_0;
    }
  });
  Object.defineProperty(KlockLocale.prototype, 'formatTimeShort', {
    get: function () {
      return this.formatTimeShort_o9xygi$_0;
    }
  });
  function KlockLocale$Companion() {
    KlockLocale$Companion_instance = this;
  }
  Object.defineProperty(KlockLocale$Companion.prototype, 'english', {
    get: function () {
      return KlockLocale$English$Companion_getInstance();
    }
  });
  Object.defineProperty(KlockLocale$Companion.prototype, 'default', {
    get: function () {
      return KlockLocale_default;
    },
    set: function (value) {
      KlockLocale_default = value;
    }
  });
  KlockLocale$Companion.prototype.setTemporarily_rl52rq$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.KlockLocale.Companion.setTemporarily_rl52rq$', function (locale, callback) {
    var old = this.default;
    this.default = locale;
    try {
      return callback();
    }
    finally {
      this.default = old;
    }
  });
  KlockLocale$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var KlockLocale$Companion_instance = null;
  function KlockLocale$Companion_getInstance() {
    if (KlockLocale$Companion_instance === null) {
      new KlockLocale$Companion();
    }
    return KlockLocale$Companion_instance;
  }
  function KlockLocale$English() {
    KlockLocale$English$Companion_getInstance();
    KlockLocale.call(this);
    this.ISO639_1_dga5i1$_0 = 'en';
    this.firstDayOfWeek_s7n0fb$_0 = DayOfWeek$Sunday_getInstance();
    this.daysOfWeek_fz9w6m$_0 = listOf(['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday']);
    this.months_gzm3nh$_0 = listOf(['january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december']);
    this.formatTimeMedium_877xvb$_0 = this.format_61zpoe$('h:mm:ss a');
    this.formatTimeShort_cvrowk$_0 = this.format_61zpoe$('h:mm a');
  }
  function KlockLocale$English$Companion() {
    KlockLocale$English$Companion_instance = this;
    KlockLocale$English.call(this);
  }
  KlockLocale$English$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [KlockLocale$English]
  };
  var KlockLocale$English$Companion_instance = null;
  function KlockLocale$English$Companion_getInstance() {
    if (KlockLocale$English$Companion_instance === null) {
      new KlockLocale$English$Companion();
    }
    return KlockLocale$English$Companion_instance;
  }
  Object.defineProperty(KlockLocale$English.prototype, 'ISO639_1', {
    get: function () {
      return this.ISO639_1_dga5i1$_0;
    }
  });
  Object.defineProperty(KlockLocale$English.prototype, 'firstDayOfWeek', {
    get: function () {
      return this.firstDayOfWeek_s7n0fb$_0;
    }
  });
  Object.defineProperty(KlockLocale$English.prototype, 'daysOfWeek', {
    get: function () {
      return this.daysOfWeek_fz9w6m$_0;
    }
  });
  Object.defineProperty(KlockLocale$English.prototype, 'months', {
    get: function () {
      return this.months_gzm3nh$_0;
    }
  });
  Object.defineProperty(KlockLocale$English.prototype, 'formatTimeMedium', {
    get: function () {
      return this.formatTimeMedium_877xvb$_0;
    }
  });
  Object.defineProperty(KlockLocale$English.prototype, 'formatTimeShort', {
    get: function () {
      return this.formatTimeShort_cvrowk$_0;
    }
  });
  KlockLocale$English.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'English',
    interfaces: [KlockLocale]
  };
  function KlockLocale$monthsShort$lambda(this$KlockLocale) {
    return function () {
      var $receiver = this$KlockLocale.months;
      var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var item = tmp$.next();
        destination.add_11rb$(substr(item, 0, 3));
      }
      return destination;
    };
  }
  function KlockLocale$daysOfWeekShort$lambda(this$KlockLocale) {
    return function () {
      var $receiver = this$KlockLocale.daysOfWeek;
      var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var item = tmp$.next();
        destination.add_11rb$(substr(item, 0, 3));
      }
      return destination;
    };
  }
  KlockLocale.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KlockLocale',
    interfaces: []
  };
  var measureTime = defineInlineFunction('klock-root-klock.com.soywiz.klock.measureTime_o14v8n$', wrapFunction(function () {
    var klock = _.com.soywiz.klock;
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function (callback) {
      var start = klock.PerformanceCounter.microseconds;
      callback();
      var end = klock.PerformanceCounter.microseconds;
      var $receiver = end - start;
      return TimeSpan.Companion.fromMicroseconds_14dthe$(numberToDouble($receiver));
    };
  }));
  var measureTimeWithResult = defineInlineFunction('klock-root-klock.com.soywiz.klock.measureTimeWithResult_9ce4rd$', wrapFunction(function () {
    var throwUPAE = Kotlin.throwUPAE;
    var TimedResult_init = _.com.soywiz.klock.TimedResult;
    var klock = _.com.soywiz.klock;
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function (callback) {
      var result = {v: null};
      var start = klock.PerformanceCounter.microseconds;
      result.v = callback();
      var end = klock.PerformanceCounter.microseconds;
      var $receiver = end - start;
      var elapsed = TimeSpan.Companion.fromMicroseconds_14dthe$(numberToDouble($receiver));
      return new TimedResult_init(result.v == null ? throwUPAE('result') : result.v, elapsed);
    };
  }));
  function TimedResult(result, time) {
    this.result = result;
    this.time = time;
  }
  TimedResult.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimedResult',
    interfaces: []
  };
  TimedResult.prototype.component1 = function () {
    return this.result;
  };
  TimedResult.prototype.component2 = function () {
    return this.time;
  };
  TimedResult.prototype.copy_lx99sw$ = function (result, time) {
    return new TimedResult(result === void 0 ? this.result : result, time === void 0 ? this.time : time);
  };
  TimedResult.prototype.toString = function () {
    return 'TimedResult(result=' + Kotlin.toString(this.result) + (', time=' + Kotlin.toString(this.time)) + ')';
  };
  TimedResult.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.result) | 0;
    result = result * 31 + Kotlin.hashCode(this.time) | 0;
    return result;
  };
  TimedResult.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.result, other.result) && Kotlin.equals(this.time, other.time)))));
  };
  function Month(name, ordinal, index1, daysCommon, daysLeap) {
    if (daysLeap === void 0)
      daysLeap = daysCommon;
    Enum.call(this);
    this.index1 = index1;
    this.daysCommon = daysCommon;
    this.daysLeap = daysLeap;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function Month_initFields() {
    Month_initFields = function () {
    };
    Month$January_instance = new Month('January', 0, 1, 31);
    Month$February_instance = new Month('February', 1, 2, 28, 29);
    Month$March_instance = new Month('March', 2, 3, 31);
    Month$April_instance = new Month('April', 3, 4, 30);
    Month$May_instance = new Month('May', 4, 5, 31);
    Month$June_instance = new Month('June', 5, 6, 30);
    Month$July_instance = new Month('July', 6, 7, 31);
    Month$August_instance = new Month('August', 7, 8, 31);
    Month$September_instance = new Month('September', 8, 9, 30);
    Month$October_instance = new Month('October', 9, 10, 31);
    Month$November_instance = new Month('November', 10, 11, 30);
    Month$December_instance = new Month('December', 11, 12, 31);
    Month$Companion_getInstance();
  }
  var Month$January_instance;
  function Month$January_getInstance() {
    Month_initFields();
    return Month$January_instance;
  }
  var Month$February_instance;
  function Month$February_getInstance() {
    Month_initFields();
    return Month$February_instance;
  }
  var Month$March_instance;
  function Month$March_getInstance() {
    Month_initFields();
    return Month$March_instance;
  }
  var Month$April_instance;
  function Month$April_getInstance() {
    Month_initFields();
    return Month$April_instance;
  }
  var Month$May_instance;
  function Month$May_getInstance() {
    Month_initFields();
    return Month$May_instance;
  }
  var Month$June_instance;
  function Month$June_getInstance() {
    Month_initFields();
    return Month$June_instance;
  }
  var Month$July_instance;
  function Month$July_getInstance() {
    Month_initFields();
    return Month$July_instance;
  }
  var Month$August_instance;
  function Month$August_getInstance() {
    Month_initFields();
    return Month$August_instance;
  }
  var Month$September_instance;
  function Month$September_getInstance() {
    Month_initFields();
    return Month$September_instance;
  }
  var Month$October_instance;
  function Month$October_getInstance() {
    Month_initFields();
    return Month$October_instance;
  }
  var Month$November_instance;
  function Month$November_getInstance() {
    Month_initFields();
    return Month$November_instance;
  }
  var Month$December_instance;
  function Month$December_getInstance() {
    Month_initFields();
    return Month$December_instance;
  }
  Object.defineProperty(Month.prototype, 'index0', {
    get: function () {
      return this.index1 - 1 | 0;
    }
  });
  Month.prototype.days_6taknv$ = function (leap) {
    return leap ? this.daysLeap : this.daysCommon;
  };
  Month.prototype.days_za3lpa$ = function (year) {
    return this.days_6taknv$((new Year(year)).isLeap);
  };
  Month.prototype.days_ccxljp$ = function (year) {
    return this.days_6taknv$(year.isLeap);
  };
  Month.prototype.daysToStart_6taknv$ = function (leap) {
    return Month$Companion_getInstance().YEAR_DAYS_0(leap)[this.index0];
  };
  Month.prototype.daysToStart_za3lpa$ = function (year) {
    return this.daysToStart_6taknv$((new Year(year)).isLeap);
  };
  Month.prototype.daysToStart_ccxljp$ = function (year) {
    return this.daysToStart_6taknv$(year.isLeap);
  };
  Month.prototype.daysToEnd_6taknv$ = function (leap) {
    return Month$Companion_getInstance().YEAR_DAYS_0(leap)[this.index1];
  };
  Month.prototype.daysToEnd_za3lpa$ = function (year) {
    return this.daysToEnd_6taknv$((new Year(year)).isLeap);
  };
  Month.prototype.daysToEnd_ccxljp$ = function (year) {
    return this.daysToEnd_6taknv$(year.isLeap);
  };
  Object.defineProperty(Month.prototype, 'previous', {
    get: function () {
      return this.minus_za3lpa$(1);
    }
  });
  Object.defineProperty(Month.prototype, 'next', {
    get: function () {
      return this.plus_za3lpa$(1);
    }
  });
  Month.prototype.plus_za3lpa$ = function (delta) {
    return Month$Companion_getInstance().get_za3lpa$(this.index1 + delta | 0);
  };
  Month.prototype.minus_za3lpa$ = function (delta) {
    return Month$Companion_getInstance().get_za3lpa$(this.index1 - delta | 0);
  };
  Month.prototype.minus_s5s5ke$ = function (other) {
    return abs(this.index0 - other.index0 | 0);
  };
  Object.defineProperty(Month.prototype, 'localName', {
    get: function () {
      return this.localName_kdekv2$(KlockLocale$Companion_getInstance().default);
    }
  });
  Month.prototype.localName_kdekv2$ = function (locale) {
    return locale.months.get_za3lpa$(this.index0);
  };
  Object.defineProperty(Month.prototype, 'localShortName', {
    get: function () {
      return this.localShortName_kdekv2$(KlockLocale$Companion_getInstance().default);
    }
  });
  Month.prototype.localShortName_kdekv2$ = function (locale) {
    return locale.monthsShort.get_za3lpa$(this.index0);
  };
  function Month$Companion() {
    Month$Companion_instance = this;
    this.Count = 12;
    this.BY_INDEX0_0 = Month$values();
    this.YEAR_DAYS_LEAP_0 = this.generateDaysToStart_0(true);
    this.YEAR_DAYS_COMMON_0 = this.generateDaysToStart_0(false);
  }
  Month$Companion.prototype.invoke_za3lpa$ = function (index1) {
    return this.adjusted_za3lpa$(index1);
  };
  Month$Companion.prototype.get_za3lpa$ = function (index1) {
    return this.adjusted_za3lpa$(index1);
  };
  Month$Companion.prototype.adjusted_za3lpa$ = function (index1) {
    return this.BY_INDEX0_0[umod(index1 - 1 | 0, 12)];
  };
  Month$Companion.prototype.checked_za3lpa$ = function (index1) {
    var tmp$ = this.BY_INDEX0_0;
    if (!(1 <= index1 && index1 <= 12))
      throw new DateException('Month ' + index1 + ' not in 1..12');
    return tmp$[index1 - 1 | 0];
  };
  Month$Companion.prototype.fromDayOfYear_fzusl$ = function (dayOfYear, leap) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    var days = this.YEAR_DAYS_0(leap);
    var day0 = dayOfYear - 1 | 0;
    var guess = day0 / 32 | 0;
    if (0 <= guess && guess <= 11) {
      tmp$ = days[guess];
      tmp$_0 = days[guess + 1 | 0];
      tmp$_1 = (tmp$ <= day0 && day0 < tmp$_0);
    }
     else
      tmp$_1 = false;
    if (tmp$_1)
      return Month$Companion_getInstance().get_za3lpa$(guess + 1 | 0);
    if (0 <= guess && guess <= 10) {
      tmp$_2 = days[guess + 1 | 0];
      tmp$_3 = days[guess + 2 | 0];
      tmp$_4 = (tmp$_2 <= day0 && day0 < tmp$_3);
    }
     else
      tmp$_4 = false;
    if (tmp$_4)
      return Month$Companion_getInstance().get_za3lpa$(guess + 2 | 0);
    return null;
  };
  Month$Companion.prototype.fromDayOfYear_7t9d$ = function (dayOfYear, year) {
    return this.fromDayOfYear_fzusl$(dayOfYear, year.isLeap);
  };
  Month$Companion.prototype.YEAR_DAYS_0 = function (isLeap) {
    return isLeap ? this.YEAR_DAYS_LEAP_0 : this.YEAR_DAYS_COMMON_0;
  };
  Month$Companion.prototype.generateDaysToStart_0 = function (leap) {
    var total = {v: 0};
    var array = new Int32Array(13);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      total.v = total.v + (i === 0 ? 0 : this.BY_INDEX0_0[i - 1 | 0].days_6taknv$(leap)) | 0;
      array[i] = total.v;
    }
    return array;
  };
  Month$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Month$Companion_instance = null;
  function Month$Companion_getInstance() {
    Month_initFields();
    if (Month$Companion_instance === null) {
      new Month$Companion();
    }
    return Month$Companion_instance;
  }
  Month.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Month',
    interfaces: [Enum]
  };
  function Month$values() {
    return [Month$January_getInstance(), Month$February_getInstance(), Month$March_getInstance(), Month$April_getInstance(), Month$May_getInstance(), Month$June_getInstance(), Month$July_getInstance(), Month$August_getInstance(), Month$September_getInstance(), Month$October_getInstance(), Month$November_getInstance(), Month$December_getInstance()];
  }
  Month.values = Month$values;
  function Month$valueOf(name) {
    switch (name) {
      case 'January':
        return Month$January_getInstance();
      case 'February':
        return Month$February_getInstance();
      case 'March':
        return Month$March_getInstance();
      case 'April':
        return Month$April_getInstance();
      case 'May':
        return Month$May_getInstance();
      case 'June':
        return Month$June_getInstance();
      case 'July':
        return Month$July_getInstance();
      case 'August':
        return Month$August_getInstance();
      case 'September':
        return Month$September_getInstance();
      case 'October':
        return Month$October_getInstance();
      case 'November':
        return Month$November_getInstance();
      case 'December':
        return Month$December_getInstance();
      default:throwISE('No enum constant com.soywiz.klock.Month.' + name);
    }
  }
  Month.valueOf_61zpoe$ = Month$valueOf;
  var get_years = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_years_s8ev3n$', wrapFunction(function () {
    var MonthSpan_init = _.com.soywiz.klock.MonthSpan;
    return function ($receiver) {
      return new MonthSpan_init(12 * $receiver | 0);
    };
  }));
  var get_months = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_months_s8ev3n$', wrapFunction(function () {
    var MonthSpan_init = _.com.soywiz.klock.MonthSpan;
    return function ($receiver) {
      return new MonthSpan_init($receiver);
    };
  }));
  function MonthSpan(totalMonths) {
    this.totalMonths = totalMonths;
  }
  MonthSpan.prototype.unaryMinus = function () {
    return new MonthSpan(-this.totalMonths | 0);
  };
  MonthSpan.prototype.unaryPlus = function () {
    return new MonthSpan(+this.totalMonths);
  };
  MonthSpan.prototype.plus_fv8bff$ = function (other) {
    return new DateTimeSpan(this, other);
  };
  MonthSpan.prototype.plus_glepj8$ = function (other) {
    return new MonthSpan(this.totalMonths + other.totalMonths | 0);
  };
  MonthSpan.prototype.plus_5gml0z$ = function (other) {
    return new DateTimeSpan(other.monthSpan.plus_glepj8$(this), other.timeSpan);
  };
  MonthSpan.prototype.minus_fv8bff$ = function (other) {
    return this.plus_fv8bff$(other.unaryMinus());
  };
  MonthSpan.prototype.minus_glepj8$ = function (other) {
    return this.plus_glepj8$(other.unaryMinus());
  };
  MonthSpan.prototype.minus_5gml0z$ = function (other) {
    return this.plus_5gml0z$(other.unaryMinus());
  };
  MonthSpan.prototype.times_3p81yu$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.MonthSpan.times_3p81yu$', wrapFunction(function () {
    var numberToDouble = Kotlin.numberToDouble;
    var numberToInt = Kotlin.numberToInt;
    var MonthSpan_init = _.com.soywiz.klock.MonthSpan;
    return function (times) {
      return new MonthSpan_init(numberToInt(this.totalMonths * numberToDouble(times)));
    };
  }));
  MonthSpan.prototype.div_3p81yu$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.MonthSpan.div_3p81yu$', wrapFunction(function () {
    var numberToDouble = Kotlin.numberToDouble;
    var numberToInt = Kotlin.numberToInt;
    var MonthSpan_init = _.com.soywiz.klock.MonthSpan;
    return function (times) {
      return new MonthSpan_init(numberToInt(this.totalMonths / numberToDouble(times)));
    };
  }));
  MonthSpan.prototype.compareTo_11rb$ = function (other) {
    return Kotlin.primitiveCompareTo(this.totalMonths, other.totalMonths);
  };
  MonthSpan.prototype.toString = function () {
    var list = ArrayList_init();
    if (get_years_0(this) !== 0)
      list.add_11rb$(get_years_0(this).toString() + 'Y');
    if (get_months_0(this) !== 0 || get_years_0(this) === 0)
      list.add_11rb$(get_months_0(this).toString() + 'M');
    return joinToString(list, ' ');
  };
  MonthSpan.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MonthSpan',
    interfaces: [Comparable]
  };
  MonthSpan.prototype.unbox = function () {
    return this.totalMonths;
  };
  MonthSpan.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.totalMonths) | 0;
    return result;
  };
  MonthSpan.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.totalMonths, other.totalMonths))));
  };
  function get_totalYears($receiver) {
    return $receiver.totalMonths / 12.0;
  }
  function get_years_0($receiver) {
    return $receiver.totalMonths / 12 | 0;
  }
  function get_months_0($receiver) {
    return $receiver.totalMonths % 12;
  }
  function PatternDateFormat(format, locale) {
    PatternDateFormat$Companion_getInstance();
    this.format = format;
    this.locale = locale;
    this.parts_0 = ArrayList_init();
    this.escapedFormat_0 = Regex.Companion.escapeReplacement_61zpoe$(this.format);
    var $receiver = this.escapedFormat_0;
    var regex = PatternDateFormat$Companion_getInstance().rx_0;
    var replace_20wsma$result;
    replace_20wsma$break: do {
      var match = regex.find_905azu$($receiver);
      if (match == null) {
        replace_20wsma$result = $receiver.toString();
        break replace_20wsma$break;
      }
      var lastStart = 0;
      var length = $receiver.length;
      var sb = StringBuilder_init_0(length);
      do {
        var foundMatch = ensureNotNull(match);
        sb.append_ezbsdh$($receiver, lastStart, foundMatch.range.start);
        var tmp$ = sb.append_gw00v9$;
        var transform$result;
        var v = foundMatch.groupValues.get_za3lpa$(0);
        this.parts_0.add_11rb$(v);
        if (startsWith(v, "'")) {
          transform$result = '(' + Regex.Companion.escapeReplacement_61zpoe$(trim(v, Kotlin.charArrayOf(39))) + ')';
        }
         else if (startsWith(v, 'X', true)) {
          transform$result = '([Z]|[+-]\\d\\d|[+-]\\d\\d\\d\\d|[+-]\\d\\d:\\d\\d)?';
        }
         else {
          transform$result = '([\\w\\+\\-]*[^Z+-\\.])';
        }
        tmp$.call(sb, transform$result);
        lastStart = foundMatch.range.endInclusive + 1 | 0;
        match = foundMatch.next();
      }
       while (lastStart < length && match != null);
      if (lastStart < length) {
        sb.append_ezbsdh$($receiver, lastStart, length);
      }
      replace_20wsma$result = sb.toString();
    }
     while (false);
    this.rx2_0 = Regex_init('^' + replace_20wsma$result + '$');
    this.parts2_0 = splitKeep(this.escapedFormat_0, PatternDateFormat$Companion_getInstance().rx_0);
  }
  Object.defineProperty(PatternDateFormat.prototype, 'realLocale', {
    get: function () {
      var tmp$;
      return (tmp$ = this.locale) != null ? tmp$ : KlockLocale$Companion_getInstance().default;
    }
  });
  function PatternDateFormat$Companion() {
    PatternDateFormat$Companion_instance = this;
    this.rx_hkb9o5$_0 = lazy(PatternDateFormat$Companion$rx$lambda);
  }
  Object.defineProperty(PatternDateFormat$Companion.prototype, 'rx_0', {
    get: function () {
      return this.rx_hkb9o5$_0.value;
    }
  });
  function PatternDateFormat$Companion$rx$lambda() {
    return Regex_init("('[\\w]+'|[\\w]+\\B[^Xx]|[Xx]{1,3}|[\\w]+)");
  }
  PatternDateFormat$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PatternDateFormat$Companion_instance = null;
  function PatternDateFormat$Companion_getInstance() {
    if (PatternDateFormat$Companion_instance === null) {
      new PatternDateFormat$Companion();
    }
    return PatternDateFormat$Companion_instance;
  }
  PatternDateFormat.prototype.withLocale_7qgj31$ = function (locale) {
    return new PatternDateFormat(this.format, locale);
  };
  PatternDateFormat.prototype.format_j01w8f$ = function (dd) {
    var tmp$, tmp$_0, tmp$_1;
    var utc = dd.local;
    var out = '';
    tmp$ = this.parts2_0.iterator();
    while (tmp$.hasNext()) {
      var name2 = tmp$.next();
      var name = trim(name2, Kotlin.charArrayOf(39));
      tmp$_1 = out;
      switch (name) {
        case 'E':
        case 'EE':
        case 'EEE':
          tmp$_0 = capitalize(this.realLocale.daysOfWeekShort.get_za3lpa$(utc.dayOfWeek.index0));
          break;
        case 'EEEE':
        case 'EEEEE':
        case 'EEEEEE':
          tmp$_0 = capitalize(this.realLocale.daysOfWeek.get_za3lpa$(utc.dayOfWeek.index0));
          break;
        case 'z':
        case 'zzz':
          tmp$_0 = dd.offset.timeZone;
          break;
        case 'd':
          tmp$_0 = utc.dayOfMonth.toString();
          break;
        case 'dd':
          tmp$_0 = padded(utc.dayOfMonth, 2);
          break;
        case 'M':
          tmp$_0 = padded(utc.month1, 1);
          break;
        case 'MM':
          tmp$_0 = padded(utc.month1, 2);
          break;
        case 'MMM':
          tmp$_0 = capitalize(substr(this.realLocale.months.get_za3lpa$(utc.month0), 0, 3));
          break;
        case 'MMMM':
          tmp$_0 = capitalize(this.realLocale.months.get_za3lpa$(utc.month0));
          break;
        case 'MMMMM':
          tmp$_0 = capitalize(substr(this.realLocale.months.get_za3lpa$(utc.month0), 0, 1));
          break;
        case 'y':
          tmp$_0 = utc.yearInt;
          break;
        case 'yy':
          tmp$_0 = padded(utc.yearInt % 100, 2);
          break;
        case 'yyy':
          tmp$_0 = padded(utc.yearInt % 1000, 3);
          break;
        case 'yyyy':
          tmp$_0 = padded(utc.yearInt, 4);
          break;
        case 'YYYY':
          tmp$_0 = padded(utc.yearInt, 4);
          break;
        case 'H':
          tmp$_0 = padded(utc.hours, 1);
          break;
        case 'HH':
          tmp$_0 = padded(utc.hours, 2);
          break;
        case 'h':
          tmp$_0 = padded((12 + utc.hours | 0) % 12, 1);
          break;
        case 'hh':
          tmp$_0 = padded((12 + utc.hours | 0) % 12, 2);
          break;
        case 'm':
          tmp$_0 = padded(utc.minutes, 1);
          break;
        case 'mm':
          tmp$_0 = padded(utc.minutes, 2);
          break;
        case 's':
          tmp$_0 = padded(utc.seconds, 1);
          break;
        case 'ss':
          tmp$_0 = padded(utc.seconds, 2);
          break;
        case 'S':
        case 'SS':
        case 'SSS':
        case 'SSSS':
        case 'SSSSS':
        case 'SSSSSS':
          var milli = utc.milliseconds;
          var x = utc.milliseconds;
          var base10length = numberToInt(Math_0.log10(x)) + 1 | 0;
          if (base10length > name.length) {
            var n = -1 * (base10length - name.length | 0) | 0;
            var fractionalPart = numberToInt(milli * Math_0.pow(10.0, n));
            tmp$_0 = fractionalPart;
          }
           else {
            var fractionalPart_0 = padded(milli, 3) + '000';
            tmp$_0 = substr(fractionalPart_0, 0, name.length);
          }

          break;
        case 'X':
        case 'XX':
        case 'XXX':
        case 'x':
        case 'xx':
        case 'xxx':
          if (startsWith(name, 'X') && dd.offset.totalMinutesInt === 0)
            tmp$_0 = 'Z';
          else {
            var p = dd.offset.totalMinutesInt >= 0 ? '+' : '-';
            var hours = abs(dd.offset.totalMinutesInt / 60 | 0);
            var minutes = abs(dd.offset.totalMinutesInt % 60);
            switch (name) {
              case 'X':
              case 'x':
                tmp$_0 = p + padded(hours, 2);
                break;
              case 'XX':
              case 'xx':
                tmp$_0 = p + padded(hours, 2) + padded(minutes, 2);
                break;
              case 'XXX':
              case 'xxx':
                tmp$_0 = p + padded(hours, 2) + ':' + padded(minutes, 2);
                break;
              default:tmp$_0 = name;
                break;
            }
          }

          break;
        case 'a':
          tmp$_0 = utc.hours < 12 ? 'am' : 'pm';
          break;
        default:tmp$_0 = name;
          break;
      }
      out = tmp$_1 + tmp$_0;
    }
    return out;
  };
  PatternDateFormat.prototype.tryParse_ivxn3r$$default = function (str, doThrow) {
    var tmp$, tmp$_0, tmp$_1;
    var millisecond = 0;
    var second = 0;
    var minute = 0;
    var hour = 0;
    var day = 1;
    var month = 1;
    var fullYear = 1970;
    var offset = null;
    var isPm = false;
    var is12HourFormat = false;
    tmp$ = this.rx2_0.find_905azu$(str);
    if (tmp$ == null) {
      return null;
    }
    var result = tmp$;
    tmp$_0 = zip(this.parts_0, drop(result.groupValues, 1)).iterator();
    while (tmp$_0.hasNext()) {
      var tmp$_2 = tmp$_0.next();
      var name = tmp$_2.component1()
      , value = tmp$_2.component2();
      switch (name) {
        case 'E':
        case 'EE':
        case 'EEE':
        case 'EEEE':
        case 'EEEEE':
        case 'EEEEEE':
          break;
        case 'z':
        case 'zzz':
          break;
        case 'd':
        case 'dd':
          day = toInt(value);
          break;
        case 'M':
        case 'MM':
          month = toInt(value);
          break;
        case 'MMM':
          month = this.realLocale.monthsShort.indexOf_11rb$(value.toLowerCase()) + 1 | 0;
          break;
        case 'y':
        case 'yyyy':
        case 'YYYY':
          fullYear = toInt(value);
          break;
        case 'yy':
          if (doThrow)
            throw RuntimeException_init('Not guessing years from two digits.');
          else
            return null;
        case 'yyy':
          fullYear = toInt(value) + (toInt(value) < 800 ? 2000 : 1000) | 0;
          break;
        case 'H':
        case 'HH':
          hour = toInt(value);
          break;
        case 'm':
        case 'mm':
          minute = toInt(value);
          break;
        case 's':
        case 'ss':
          second = toInt(value);
          break;
        case 'S':
        case 'SS':
        case 'SSS':
        case 'SSSS':
        case 'SSSSS':
        case 'SSSSSS':
          var x = toDouble(value);
          var base10length = numberToInt(Math_0.log10(x)) + 1 | 0;
          if (base10length > 3) {
            var tmp$_3 = toDouble(value);
            var n = -1 * (base10length - 3 | 0) | 0;
            tmp$_1 = numberToInt(tmp$_3 * Math_0.pow(10.0, n));
          }
           else {
            tmp$_1 = toInt(value);
          }

          millisecond = tmp$_1;
          break;
        case 'X':
        case 'XX':
        case 'XXX':
        case 'x':
        case 'xx':
        case 'xxx':
          if (startsWith(name, 'X') && first(value) === 90)
            offset = 0;
          else if (startsWith(name, 'x') && first(value) === 90)
            if (doThrow)
              throw RuntimeException_init('Zulu Time Zone is only accepted with X-XXX formats.');
            else
              return null;
          else if (first(value) !== 90) {
            var hours = toInt(substringBefore(drop_0(value, 1), 58));
            var minutes = toInt(substringAfter(value, 58, '0'));
            offset = (hours * 60 | 0) + minutes | 0;
            if (first(value) === 45) {
              offset = -offset | 0;
            }
          }

          break;
        case 'MMMM':
          month = this.realLocale.months.indexOf_11rb$(value.toLowerCase()) + 1 | 0;
          break;
        case 'MMMMM':
          if (doThrow)
            throw RuntimeException_init('Not possible to get the month from one letter.');
          else
            return null;
        case 'h':
        case 'hh':
          hour = toInt(value);
          is12HourFormat = true;
          break;
        case 'a':
          isPm = equals(value, 'pm');
          break;
        default:break;
      }
    }
    if (is12HourFormat && isPm) {
      hour = hour + 12 | 0;
    }
    var dateTime = DateTime$Companion_getInstance().createAdjusted_ui44o2$(fullYear, month, day, hour, minute, second, millisecond);
    var $receiver = offset != null ? offset : 0;
    return dateTime.toOffsetUnadjusted_fv8bff$(TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble($receiver)));
  };
  PatternDateFormat.prototype.toString = function () {
    return this.format;
  };
  PatternDateFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PatternDateFormat',
    interfaces: [DateFormat]
  };
  function PatternDateFormat_init(format, $this) {
    $this = $this || Object.create(PatternDateFormat.prototype);
    PatternDateFormat.call($this, format, null);
    return $this;
  }
  function PerformanceCounter() {
    PerformanceCounter_instance = this;
  }
  Object.defineProperty(PerformanceCounter.prototype, 'nanoseconds', {
    get: function () {
      return KlockInternal_getInstance().microClock * 1000.0;
    }
  });
  Object.defineProperty(PerformanceCounter.prototype, 'microseconds', {
    get: function () {
      return KlockInternal_getInstance().microClock;
    }
  });
  Object.defineProperty(PerformanceCounter.prototype, 'milliseconds', {
    get: function () {
      return KlockInternal_getInstance().microClock / 1000.0;
    }
  });
  Object.defineProperty(PerformanceCounter.prototype, 'reference', {
    get: function () {
      var $receiver = KlockInternal_getInstance().microClock;
      return TimeSpan.Companion.fromMicroseconds_14dthe$(numberToDouble($receiver));
    }
  });
  PerformanceCounter.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'PerformanceCounter',
    interfaces: []
  };
  var PerformanceCounter_instance = null;
  function PerformanceCounter_getInstance() {
    if (PerformanceCounter_instance === null) {
      new PerformanceCounter();
    }
    return PerformanceCounter_instance;
  }
  function TimeFormat() {
  }
  TimeFormat.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TimeFormat',
    interfaces: []
  };
  function parse_1($receiver, str) {
    var tmp$;
    tmp$ = $receiver.tryParse_ivxn3r$(str, true);
    if (tmp$ == null) {
      throw new DateException("Not a valid format: '" + str + "' for '" + $receiver + "'");
    }
    return tmp$;
  }
  function TimeProvider() {
    TimeProvider$Companion_getInstance();
  }
  function TimeProvider$Companion() {
    TimeProvider$Companion_instance = this;
  }
  TimeProvider$Companion.prototype.now = function () {
    return DateTime$Companion_getInstance().now();
  };
  function TimeProvider$Companion$invoke$ObjectLiteral(closure$callback) {
    this.closure$callback = closure$callback;
  }
  TimeProvider$Companion$invoke$ObjectLiteral.prototype.now = function () {
    return this.closure$callback();
  };
  TimeProvider$Companion$invoke$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [TimeProvider]
  };
  TimeProvider$Companion.prototype.invoke_x2enld$ = function (callback) {
    return new TimeProvider$Companion$invoke$ObjectLiteral(callback);
  };
  TimeProvider$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [TimeProvider]
  };
  var TimeProvider$Companion_instance = null;
  function TimeProvider$Companion_getInstance() {
    if (TimeProvider$Companion_instance === null) {
      new TimeProvider$Companion();
    }
    return TimeProvider$Companion_instance;
  }
  TimeProvider.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TimeProvider',
    interfaces: []
  };
  var get_nanoseconds = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_nanoseconds_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromNanoseconds_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_microseconds = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_microseconds_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromMicroseconds_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_milliseconds = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_milliseconds_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_seconds = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_seconds_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromSeconds_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_minutes = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_minutes_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_hours = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_hours_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromHours_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_days = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_days_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromDays_14dthe$(numberToDouble($receiver));
    };
  }));
  var get_weeks = defineInlineFunction('klock-root-klock.com.soywiz.klock.get_weeks_rcaex3$', wrapFunction(function () {
    var TimeSpan = _.com.soywiz.klock.TimeSpan;
    var numberToDouble = Kotlin.numberToDouble;
    return function ($receiver) {
      return TimeSpan.Companion.fromWeeks_14dthe$(numberToDouble($receiver));
    };
  }));
  function TimeSpan(milliseconds) {
    TimeSpan$Companion_getInstance();
    this.milliseconds = milliseconds;
  }
  Object.defineProperty(TimeSpan.prototype, 'nanoseconds', {
    get: function () {
      return this.milliseconds / TimeSpan$Companion_getInstance().MILLIS_PER_NANOSECOND_0;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'microseconds', {
    get: function () {
      return this.milliseconds / TimeSpan$Companion_getInstance().MILLIS_PER_MICROSECOND_0;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'seconds', {
    get: function () {
      return this.milliseconds / 1000;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'minutes', {
    get: function () {
      return this.milliseconds / 60000;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'hours', {
    get: function () {
      return this.milliseconds / 3600000;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'days', {
    get: function () {
      return this.milliseconds / 86400000;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'weeks', {
    get: function () {
      return this.milliseconds / 604800000;
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'millisecondsLong', {
    get: function () {
      return Kotlin.Long.fromNumber(this.milliseconds);
    }
  });
  Object.defineProperty(TimeSpan.prototype, 'millisecondsInt', {
    get: function () {
      return numberToInt(this.milliseconds);
    }
  });
  TimeSpan.prototype.compareTo_11rb$ = function (other) {
    return Kotlin.compareTo(this.milliseconds, other.milliseconds);
  };
  TimeSpan.prototype.unaryMinus = function () {
    return new TimeSpan(-this.milliseconds);
  };
  TimeSpan.prototype.unaryPlus = function () {
    return new TimeSpan(+this.milliseconds);
  };
  TimeSpan.prototype.plus_fv8bff$ = function (other) {
    return new TimeSpan(this.milliseconds + other.milliseconds);
  };
  TimeSpan.prototype.plus_glepj8$ = function (other) {
    return new DateTimeSpan(other, this);
  };
  TimeSpan.prototype.plus_5gml0z$ = function (other) {
    return new DateTimeSpan(other.monthSpan, other.timeSpan.plus_fv8bff$(this));
  };
  TimeSpan.prototype.minus_fv8bff$ = function (other) {
    return this.plus_fv8bff$(other.unaryMinus());
  };
  TimeSpan.prototype.minus_glepj8$ = function (other) {
    return this.plus_glepj8$(other.unaryMinus());
  };
  TimeSpan.prototype.minus_5gml0z$ = function (other) {
    return this.plus_5gml0z$(other.unaryMinus());
  };
  TimeSpan.prototype.times_za3lpa$ = function (scale) {
    return new TimeSpan(this.milliseconds * scale);
  };
  TimeSpan.prototype.times_14dthe$ = function (scale) {
    return new TimeSpan(this.milliseconds * scale);
  };
  TimeSpan.prototype.div_fv8bff$ = function (other) {
    return this.milliseconds / other.milliseconds;
  };
  TimeSpan.prototype.rem_fv8bff$ = function (other) {
    var $receiver = this.milliseconds % other.milliseconds;
    return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
  };
  function TimeSpan$Companion() {
    TimeSpan$Companion_instance = this;
    this.MILLIS_PER_MICROSECOND_0 = 1.0 / 1000.0;
    this.MILLIS_PER_NANOSECOND_0 = this.MILLIS_PER_MICROSECOND_0 / 1000.0;
    this.ZERO = new TimeSpan(0.0);
    this.NULL = new TimeSpan(kotlin_js_internal_DoubleCompanionObject.NaN);
    this.timeSteps_0 = listOf([60, 60, 24]);
  }
  TimeSpan$Companion.prototype.fromMilliseconds_14dthe$ = function (ms) {
    if (ms === 0.0)
      return this.ZERO;
    else
      return new TimeSpan(ms);
  };
  TimeSpan$Companion.prototype.fromNanoseconds_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * this.MILLIS_PER_NANOSECOND_0);
  };
  TimeSpan$Companion.prototype.fromMicroseconds_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * this.MILLIS_PER_MICROSECOND_0);
  };
  TimeSpan$Companion.prototype.fromSeconds_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * 1000);
  };
  TimeSpan$Companion.prototype.fromMinutes_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * 60000);
  };
  TimeSpan$Companion.prototype.fromHours_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * 3600000);
  };
  TimeSpan$Companion.prototype.fromDays_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * 86400000);
  };
  TimeSpan$Companion.prototype.fromWeeks_14dthe$ = function (s) {
    return this.fromMilliseconds_14dthe$(s * 604800000);
  };
  TimeSpan$Companion.prototype.toTimeStringRaw_0 = function (totalMilliseconds, components) {
    if (components === void 0)
      components = 3;
    var tmp$;
    var x = totalMilliseconds / 1000.0;
    var timeUnit = numberToInt(Math_0.floor(x));
    var out = ArrayList_init();
    for (var n = 0; n < components; n++) {
      if (n === (components - 1 | 0)) {
        var element = padded(timeUnit, 2);
        out.add_11rb$(element);
        break;
      }
      tmp$ = getOrNull(this.timeSteps_0, n);
      if (tmp$ == null) {
        throw RuntimeException_init('Just supported ' + this.timeSteps_0.size + ' steps');
      }
      var step = tmp$;
      var cunit = timeUnit % step;
      timeUnit = timeUnit / step | 0;
      var element_0 = padded(cunit, 2);
      out.add_11rb$(element_0);
    }
    return joinToString(reversed(out), ':');
  };
  TimeSpan$Companion.prototype.toTimeString_87xbef$ = function (totalMilliseconds, components, addMilliseconds) {
    if (components === void 0)
      components = 3;
    if (addMilliseconds === void 0)
      addMilliseconds = false;
    var milliseconds = numberToInt(totalMilliseconds % 1000);
    var out = this.toTimeStringRaw_0(totalMilliseconds, components);
    return addMilliseconds ? out + '.' + milliseconds : out;
  };
  TimeSpan$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TimeSpan$Companion_instance = null;
  function TimeSpan$Companion_getInstance() {
    if (TimeSpan$Companion_instance === null) {
      new TimeSpan$Companion();
    }
    return TimeSpan$Companion_instance;
  }
  TimeSpan.prototype.toString = function () {
    return get_niceStr(this.milliseconds) + 'ms';
  };
  TimeSpan.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimeSpan',
    interfaces: [Comparable]
  };
  TimeSpan.prototype.unbox = function () {
    return this.milliseconds;
  };
  TimeSpan.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.milliseconds) | 0;
    return result;
  };
  TimeSpan.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.milliseconds, other.milliseconds))));
  };
  function toTimeString($receiver, components, addMilliseconds) {
    if (components === void 0)
      components = 3;
    if (addMilliseconds === void 0)
      addMilliseconds = false;
    return TimeSpan$Companion_getInstance().toTimeString_87xbef$($receiver.milliseconds, components, addMilliseconds);
  }
  function max_0(a, b) {
    var a_0 = a.milliseconds;
    var b_0 = b.milliseconds;
    var $receiver = Math_0.max(a_0, b_0);
    return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
  }
  function min_0(a, b) {
    var a_0 = a.milliseconds;
    var b_0 = b.milliseconds;
    var $receiver = Math_0.min(a_0, b_0);
    return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
  }
  function clamp_0($receiver, min, max) {
    if ($receiver.compareTo_11rb$(min) < 0)
      return min;
    else if ($receiver.compareTo_11rb$(max) > 0)
      return max;
    else
      return $receiver;
  }
  function TimezoneOffset(totalMilliseconds) {
    TimezoneOffset$Companion_getInstance();
    this.totalMilliseconds = totalMilliseconds;
  }
  Object.defineProperty(TimezoneOffset.prototype, 'positive', {
    get: function () {
      return this.totalMilliseconds >= 0.0;
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'time', {
    get: function () {
      var $receiver = this.totalMilliseconds;
      return TimeSpan.Companion.fromMilliseconds_14dthe$(numberToDouble($receiver));
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'totalMinutes', {
    get: function () {
      return this.totalMilliseconds / 60000;
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'totalMinutesInt', {
    get: function () {
      return numberToInt(this.totalMinutes);
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'timeZone', {
    get: function () {
      var tmp$;
      var sign = this.positive ? '+' : '-';
      var hour = padded(this.deltaHoursAbs_8be2vx$, 2);
      var minute = padded(this.deltaMinutesAbs_8be2vx$, 2);
      return ((tmp$ = this.time) != null ? tmp$.equals(TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble(0))) : null) ? 'UTC' : 'GMT' + sign + hour + minute;
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'deltaTotalMinutesAbs_0', {
    get: function () {
      return abs(numberToInt(this.totalMinutes));
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'deltaHoursAbs_8be2vx$', {
    get: function () {
      return this.deltaTotalMinutesAbs_0 / 60 | 0;
    }
  });
  Object.defineProperty(TimezoneOffset.prototype, 'deltaMinutesAbs_8be2vx$', {
    get: function () {
      return this.deltaTotalMinutesAbs_0 % 60;
    }
  });
  TimezoneOffset.prototype.toString = function () {
    return this.timeZone;
  };
  function TimezoneOffset$Companion() {
    TimezoneOffset$Companion_instance = this;
  }
  TimezoneOffset$Companion.prototype.invoke_fv8bff$ = function (time) {
    return new TimezoneOffset(time.milliseconds);
  };
  TimezoneOffset$Companion.prototype.local_mw5vjr$ = function (time) {
    return get_offset(KlockInternal_getInstance().localTimezoneOffsetMinutes_mw5vjr$(time));
  };
  TimezoneOffset$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TimezoneOffset$Companion_instance = null;
  function TimezoneOffset$Companion_getInstance() {
    if (TimezoneOffset$Companion_instance === null) {
      new TimezoneOffset$Companion();
    }
    return TimezoneOffset$Companion_instance;
  }
  TimezoneOffset.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimezoneOffset',
    interfaces: []
  };
  TimezoneOffset.prototype.unbox = function () {
    return this.totalMilliseconds;
  };
  TimezoneOffset.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.totalMilliseconds) | 0;
    return result;
  };
  TimezoneOffset.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.totalMilliseconds, other.totalMilliseconds))));
  };
  function get_offset($receiver) {
    return TimezoneOffset$Companion_getInstance().invoke_fv8bff$($receiver);
  }
  function Year(year) {
    Year$Companion_getInstance();
    this.year = year;
  }
  function Year$Companion() {
    Year$Companion_instance = this;
    this.DAYS_COMMON = 365;
    this.DAYS_LEAP = 366;
    this.LEAP_PER_4_YEARS_0 = 1;
    this.LEAP_PER_100_YEARS_0 = 24;
    this.LEAP_PER_400_YEARS_0 = 97;
    this.DAYS_PER_4_YEARS_0 = 1461;
    this.DAYS_PER_100_YEARS_0 = 36524;
    this.DAYS_PER_400_YEARS_0 = 146097;
  }
  Year$Companion.prototype.checked_za3lpa$ = function (year) {
    if (!(1 <= year && year <= 9999))
      throw new DateException('Year ' + year + ' not in 1..9999');
    return year;
  };
  Year$Companion.prototype.isLeapChecked_za3lpa$ = function (year) {
    return this.isLeap_za3lpa$(this.checked_za3lpa$(year));
  };
  Year$Companion.prototype.isLeap_za3lpa$ = function (year) {
    return year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);
  };
  Year$Companion.prototype.fromDays_za3lpa$ = function (days) {
    var v400 = days / 146097 | 0;
    var r400 = days % 146097;
    var a = r400 / 36524 | 0;
    var v100 = Math_0.min(a, 3);
    var r100 = r400 % 36524;
    var v4 = r100 / 1461 | 0;
    var r4 = r100 % 1461;
    var a_0 = r4 / 365 | 0;
    var v1 = Math_0.min(a_0, 3);
    return new Year(1 + v1 + (v4 * 4 | 0) + (v100 * 100 | 0) + (v400 * 400 | 0) | 0);
  };
  Year$Companion.prototype.days_6taknv$ = function (isLeap) {
    return isLeap ? 366 : 365;
  };
  Year$Companion.prototype.leapCountSinceOne_za3lpa$ = function (year) {
    return ((year - 1 | 0) / 4 | 0) - ((year - 1 | 0) / 100 | 0) + ((year - 1 | 0) / 400 | 0) | 0;
  };
  Year$Companion.prototype.daysSinceOne_za3lpa$ = function (year) {
    return (365 * (year - 1 | 0) | 0) + this.leapCountSinceOne_za3lpa$(year) | 0;
  };
  Year$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Year$Companion_instance = null;
  function Year$Companion_getInstance() {
    if (Year$Companion_instance === null) {
      new Year$Companion();
    }
    return Year$Companion_instance;
  }
  Object.defineProperty(Year.prototype, 'isLeapChecked', {
    get: function () {
      return Year$Companion_getInstance().isLeapChecked_za3lpa$(this.year);
    }
  });
  Object.defineProperty(Year.prototype, 'isLeap', {
    get: function () {
      return Year$Companion_getInstance().isLeap_za3lpa$(this.year);
    }
  });
  Object.defineProperty(Year.prototype, 'days', {
    get: function () {
      return Year$Companion_getInstance().days_6taknv$(this.isLeap);
    }
  });
  Object.defineProperty(Year.prototype, 'leapCountSinceOne', {
    get: function () {
      return Year$Companion_getInstance().leapCountSinceOne_za3lpa$(this.year);
    }
  });
  Object.defineProperty(Year.prototype, 'daysSinceOne', {
    get: function () {
      return Year$Companion_getInstance().daysSinceOne_za3lpa$(this.year);
    }
  });
  Year.prototype.compareTo_11rb$ = function (other) {
    return Kotlin.primitiveCompareTo(this.year, other.year);
  };
  Year.prototype.plus_za3lpa$ = function (delta) {
    return new Year(this.year + delta | 0);
  };
  Year.prototype.minus_za3lpa$ = function (delta) {
    return new Year(this.year - delta | 0);
  };
  Year.prototype.minus_ccxljp$ = function (other) {
    return this.year - other.year | 0;
  };
  Year.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Year',
    interfaces: [Comparable]
  };
  Year.prototype.unbox = function () {
    return this.year;
  };
  Year.prototype.toString = function () {
    return 'Year(year=' + Kotlin.toString(this.year) + ')';
  };
  Year.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.year) | 0;
    return result;
  };
  Year.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.year, other.year))));
  };
  function YearMonth(internalPackedInfo) {
    YearMonth$Companion_getInstance();
    this.internalPackedInfo_8be2vx$ = internalPackedInfo;
  }
  function YearMonth$Companion() {
    YearMonth$Companion_instance = this;
  }
  YearMonth$Companion.prototype.invoke_wk05xp$ = function (year, month) {
    return YearMonth$Companion_getInstance().invoke_vux9f0$(year.year, month.index1);
  };
  YearMonth$Companion.prototype.invoke_bbks$ = function (year, month) {
    return YearMonth$Companion_getInstance().invoke_vux9f0$(year, month.index1);
  };
  YearMonth$Companion.prototype.invoke_vux9f0$ = function (year, month1) {
    return new YearMonth(year << 4 | month1 & 15);
  };
  YearMonth$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var YearMonth$Companion_instance = null;
  function YearMonth$Companion_getInstance() {
    if (YearMonth$Companion_instance === null) {
      new YearMonth$Companion();
    }
    return YearMonth$Companion_instance;
  }
  Object.defineProperty(YearMonth.prototype, 'year', {
    get: function () {
      return new Year(this.yearInt);
    }
  });
  Object.defineProperty(YearMonth.prototype, 'yearInt', {
    get: function () {
      return this.internalPackedInfo_8be2vx$ >>> 4;
    }
  });
  Object.defineProperty(YearMonth.prototype, 'month', {
    get: function () {
      return Month$Companion_getInstance().get_za3lpa$(this.month1);
    }
  });
  Object.defineProperty(YearMonth.prototype, 'month1', {
    get: function () {
      return this.internalPackedInfo_8be2vx$ & 15;
    }
  });
  Object.defineProperty(YearMonth.prototype, 'days', {
    get: function () {
      return this.month.days_ccxljp$(this.year);
    }
  });
  Object.defineProperty(YearMonth.prototype, 'daysToStart', {
    get: function () {
      return this.month.daysToStart_ccxljp$(this.year);
    }
  });
  Object.defineProperty(YearMonth.prototype, 'daysToEnd', {
    get: function () {
      return this.month.daysToEnd_ccxljp$(this.year);
    }
  });
  YearMonth.prototype.plus_glepj8$ = function (span) {
    var tmp$;
    var newMonth = this.month1 + get_months_0(span) | 0;
    if (newMonth > 12)
      tmp$ = 1;
    else if (newMonth < 1)
      tmp$ = -1;
    else
      tmp$ = 0;
    var yearAdjust = tmp$;
    return YearMonth$Companion_getInstance().invoke_wk05xp$(new Year(this.yearInt + get_years_0(span) + yearAdjust | 0), Month$Companion_getInstance().get_za3lpa$(newMonth));
  };
  YearMonth.prototype.minus_glepj8$ = function (span) {
    return this.plus_glepj8$(span.unaryMinus());
  };
  YearMonth.prototype.toString = function () {
    return this.month.toString() + ' ' + this.yearInt;
  };
  YearMonth.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'YearMonth',
    interfaces: []
  };
  YearMonth.prototype.unbox = function () {
    return this.internalPackedInfo_8be2vx$;
  };
  YearMonth.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.internalPackedInfo_8be2vx$) | 0;
    return result;
  };
  YearMonth.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.internalPackedInfo_8be2vx$, other.internalPackedInfo_8be2vx$))));
  };
  function withMonth($receiver, month) {
    return YearMonth$Companion_getInstance().invoke_wk05xp$($receiver, month);
  }
  function withYear($receiver, year) {
    return YearMonth$Companion_getInstance().invoke_wk05xp$(year, $receiver);
  }
  var fastForEach = defineInlineFunction('klock-root-klock.com.soywiz.klock.internal.fastForEach_kf7q02$', function ($receiver, callback) {
    var tmp$;
    var n = 0;
    while (n < $receiver.size) {
      callback($receiver.get_za3lpa$((tmp$ = n, n = tmp$ + 1 | 0, tmp$)));
    }
  });
  function MicroStrReader(str, offset) {
    if (offset === void 0)
      offset = 0;
    this.str = str;
    this.offset = offset;
  }
  Object.defineProperty(MicroStrReader.prototype, 'length', {
    get: function () {
      return this.str.length;
    }
  });
  Object.defineProperty(MicroStrReader.prototype, 'available', {
    get: function () {
      return this.str.length - this.offset | 0;
    }
  });
  Object.defineProperty(MicroStrReader.prototype, 'hasMore', {
    get: function () {
      return this.offset < this.str.length;
    }
  });
  MicroStrReader.prototype.peekChar = function () {
    return toBoxedChar(this.str.charCodeAt(this.offset));
  };
  MicroStrReader.prototype.readChar = function () {
    var tmp$;
    return toBoxedChar(this.str.charCodeAt((tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$)));
  };
  MicroStrReader.prototype.tryRead_61zpoe$ = function (str) {
    var tmp$;
    if (str.length > this.available)
      return false;
    tmp$ = str.length;
    for (var n = 0; n < tmp$; n++)
      if (this.str.charCodeAt(this.offset + n | 0) !== str.charCodeAt(n))
        return false;
    this.offset = this.offset + str.length | 0;
    return true;
  };
  MicroStrReader.prototype.read_za3lpa$ = function (count) {
    var $receiver = this.str;
    var startIndex = this.offset;
    var endIndex = coerceAtMost(this.offset + count | 0, this.length);
    var $receiver_0 = $receiver.substring(startIndex, endIndex);
    this.offset = this.offset + $receiver_0.length | 0;
    return $receiver_0;
  };
  MicroStrReader.prototype.readInt_za3lpa$ = function (count) {
    return toInt(this.read_za3lpa$(count));
  };
  MicroStrReader.prototype.tryReadInt_za3lpa$ = function (count) {
    return toIntOrNull(this.read_za3lpa$(count));
  };
  MicroStrReader.prototype.tryReadDouble_za3lpa$ = function (count) {
    return toDoubleOrNull(replace(this.read_za3lpa$(count), 44, 46));
  };
  MicroStrReader.prototype.tryReadDouble = function () {
    var numCount = 0;
    var num = 0;
    var denCount = 0;
    var den = 0;
    var decimals = false;
    loop: while (this.hasMore) {
      var pc = unboxChar(this.peekChar());
      if (pc === 44) {
        if (numCount === 0) {
          return null;
        }
        decimals = true;
        this.readChar();
      }
       else if ((new CharRange(48, 57)).contains_mef7kx$(pc)) {
        var c = unboxChar(this.readChar());
        if (decimals) {
          denCount = denCount + 1 | 0;
          den = den * 10 | 0;
          den = den + (c - 48) | 0;
        }
         else {
          numCount = numCount + 1 | 0;
          num = num * 10 | 0;
          num = num + (c - 48) | 0;
        }
      }
       else {
        break loop;
      }
    }
    if (numCount === 0) {
      return null;
    }
    var tmp$ = num;
    var tmp$_0 = den;
    var n = -denCount | 0;
    return tmp$ + tmp$_0 * Math_0.pow(10.0, n);
  };
  MicroStrReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MicroStrReader',
    interfaces: []
  };
  function get_niceStr($receiver) {
    return Math_0.floor($receiver) === $receiver ? numberToInt($receiver).toString() : $receiver.toString();
  }
  var MILLIS_PER_SECOND;
  var MILLIS_PER_MINUTE;
  var MILLIS_PER_HOUR;
  var MILLIS_PER_DAY;
  var MILLIS_PER_WEEK;
  function padded($receiver, count) {
    return padStart($receiver.toString(), count, 48);
  }
  function padded_0($receiver, intCount, decCount) {
    var intPart = numberToInt(Math_0.floor($receiver));
    var decPart = numberToInt(round(($receiver - intPart) * Math_0.pow(10.0, decCount)));
    return substr(padded(intPart, intCount), -intCount | 0, intCount) + '.' + substr(padEnd(decPart.toString(), decCount, 48), 0, decCount);
  }
  function substr($receiver, start, length) {
    var low = clamp_1(start >= 0 ? start : $receiver.length + start | 0, 0, $receiver.length);
    var high = clamp_1(length >= 0 ? low + length | 0 : $receiver.length + length | 0, 0, $receiver.length);
    return high < low ? '' : $receiver.substring(low, high);
  }
  function clamp_1($receiver, min, max) {
    return $receiver < min ? min : $receiver > max ? max : $receiver;
  }
  function cycle($receiver, min, max) {
    return umod($receiver - min | 0, max - min + 1 | 0) + min | 0;
  }
  function cycleSteps($receiver, min, max) {
    return ($receiver - min | 0) / (max - min + 1 | 0) | 0;
  }
  function splitKeep($receiver, regex) {
    var tmp$;
    var str = $receiver;
    var out = ArrayList_init();
    var lastPos = 0;
    tmp$ = regex.findAll_905azu$($receiver).iterator();
    while (tmp$.hasNext()) {
      var part = tmp$.next();
      var prange = part.range;
      if (lastPos !== prange.start) {
        var startIndex = lastPos;
        var endIndex = prange.start;
        var element = str.substring(startIndex, endIndex);
        out.add_11rb$(element);
      }
      var element_0 = substring(str, prange);
      out.add_11rb$(element_0);
      lastPos = prange.endInclusive + 1 | 0;
    }
    if (lastPos !== str.length) {
      var startIndex_0 = lastPos;
      var element_1 = str.substring(startIndex_0);
      out.add_11rb$(element_1);
    }
    return out;
  }
  function umod($receiver, that) {
    var tmp$;
    var remainder = $receiver % that;
    if (remainder < 0)
      tmp$ = remainder + that | 0;
    else
      tmp$ = remainder;
    return tmp$;
  }
  function Moduler(value) {
    this.value = value;
    var x = this.value;
    this.avalue_0 = Math_0.abs(x);
    var x_0 = this.value;
    this.sign_0 = Math_0.sign(x_0);
  }
  Moduler.prototype.double_14dthe$ = function (count) {
    var ret = this.avalue_0 / count;
    this.avalue_0 %= count;
    return Math_0.floor(ret) * this.sign_0;
  };
  Moduler.prototype.double_3p81yu$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.internal.Moduler.double_3p81yu$', wrapFunction(function () {
    var numberToDouble = Kotlin.numberToDouble;
    return function (count) {
      return this.double_14dthe$(numberToDouble(count));
    };
  }));
  Moduler.prototype.int_3p81yu$ = defineInlineFunction('klock-root-klock.com.soywiz.klock.internal.Moduler.int_3p81yu$', wrapFunction(function () {
    var numberToDouble = Kotlin.numberToDouble;
    var numberToInt = Kotlin.numberToInt;
    return function (count) {
      return numberToInt(this.double_14dthe$(numberToDouble(count)));
    };
  }));
  Moduler.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Moduler',
    interfaces: []
  };
  function intDiv($receiver, other) {
    var x = $receiver / other;
    return Math_0.floor(x);
  }
  function toDateTime($receiver) {
    return new DateTime($receiver.getTime());
  }
  function toDate($receiver) {
    return new Date($receiver.unixMillisDouble);
  }
  var isNode;
  function initialHrTime$lambda() {
    return process.hrtime();
  }
  var initialHrTime;
  function get_initialHrTime() {
    return initialHrTime.value;
  }
  function KlockInternal() {
    KlockInternal_instance = this;
  }
  Object.defineProperty(KlockInternal.prototype, 'currentTime', {
    get: function () {
      return Date.now();
    }
  });
  Object.defineProperty(KlockInternal.prototype, 'microClock', {
    get: function () {
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
      if (isNode) {
        var result = process.hrtime(get_initialHrTime());
        tmp$_0 = result[0] * 1000000;
        var x = typeof (tmp$ = result[1] / 1000) === 'number' ? tmp$ : throwCCE();
        tmp$_2 = typeof (tmp$_1 = tmp$_0 + Math_0.floor(x)) === 'number' ? tmp$_1 : throwCCE();
      }
       else {
        var x_0 = window.performance.now() * 1000;
        tmp$_2 = Math_0.floor(x_0);
      }
      return tmp$_2;
    }
  });
  KlockInternal.prototype.localTimezoneOffsetMinutes_mw5vjr$ = function (time) {
    var rtime = time.unixMillisDouble;
    var $receiver = -(new Date(rtime)).getTimezoneOffset();
    return TimeSpan.Companion.fromMinutes_14dthe$(numberToDouble($receiver));
  };
  KlockInternal.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'KlockInternal',
    interfaces: []
  };
  var KlockInternal_instance = null;
  function KlockInternal_getInstance() {
    if (KlockInternal_instance === null) {
      new KlockInternal();
    }
    return KlockInternal_instance;
  }
  function toDateTime_0($receiver) {
    return new DateTime($receiver.getTime());
  }
  function toDate_0($receiver) {
    return new Date($receiver.unixMillisDouble);
  }
  var package$com = _.com || (_.com = {});
  var package$soywiz = package$com.soywiz || (package$com.soywiz = {});
  var package$klock = package$soywiz.klock || (package$soywiz.klock = {});
  package$klock.DateException = DateException;
  Object.defineProperty(DateFormat, 'Companion', {
    get: DateFormat$Companion_getInstance
  });
  package$klock.DateFormat = DateFormat;
  package$klock.parse_mabgv2$ = parse;
  package$klock.format_tgp00u$ = format;
  package$klock.format_94zki5$ = format_0;
  $$importsForInline$$['klock-root-klock'] = _;
  package$klock.format_x6wc0j$ = format_1;
  Object.defineProperty(DateTime$Companion$DatePart, 'Year', {
    get: DateTime$Companion$DatePart$Year_getInstance
  });
  Object.defineProperty(DateTime$Companion$DatePart, 'DayOfYear', {
    get: DateTime$Companion$DatePart$DayOfYear_getInstance
  });
  Object.defineProperty(DateTime$Companion$DatePart, 'Month', {
    get: DateTime$Companion$DatePart$Month_getInstance
  });
  Object.defineProperty(DateTime$Companion$DatePart, 'Day', {
    get: DateTime$Companion$DatePart$Day_getInstance
  });
  DateTime$Companion.prototype.DatePart = DateTime$Companion$DatePart;
  Object.defineProperty(DateTime, 'Companion', {
    get: DateTime$Companion_getInstance
  });
  package$klock.DateTime = DateTime;
  package$klock.max_4p96hy$ = max;
  package$klock.min_4p96hy$ = min;
  package$klock.clamp_qlexwg$ = clamp;
  package$klock.DateTimeRange = DateTimeRange;
  package$klock.rangeTo_68nrlv$ = rangeTo;
  package$klock.until_68nrlv$ = until;
  package$klock.DateTimeSpan_init_7k5cs4$ = DateTimeSpan_init;
  package$klock.DateTimeSpan = DateTimeSpan;
  package$klock.DateTimeSpanFormat = DateTimeSpanFormat;
  package$klock.format_7rbe1s$ = format_2;
  package$klock.format_lolva9$ = format_3;
  package$klock.parse_xf9cft$ = parse_0;
  Object.defineProperty(DateTimeTz, 'Companion', {
    get: DateTimeTz$Companion_getInstance
  });
  package$klock.DateTimeTz = DateTimeTz;
  Object.defineProperty(DayOfWeek, 'Sunday', {
    get: DayOfWeek$Sunday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Monday', {
    get: DayOfWeek$Monday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Tuesday', {
    get: DayOfWeek$Tuesday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Wednesday', {
    get: DayOfWeek$Wednesday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Thursday', {
    get: DayOfWeek$Thursday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Friday', {
    get: DayOfWeek$Friday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Saturday', {
    get: DayOfWeek$Saturday_getInstance
  });
  Object.defineProperty(DayOfWeek, 'Companion', {
    get: DayOfWeek$Companion_getInstance
  });
  package$klock.DayOfWeek = DayOfWeek;
  Object.defineProperty(ISO8601$BaseIsoTimeFormat, 'Companion', {
    get: ISO8601$BaseIsoTimeFormat$Companion_getInstance
  });
  ISO8601.prototype.BaseIsoTimeFormat = ISO8601$BaseIsoTimeFormat;
  ISO8601.prototype.BaseIsoDateTimeFormat = ISO8601$BaseIsoDateTimeFormat;
  ISO8601.prototype.IsoIntervalFormat = ISO8601$IsoIntervalFormat;
  ISO8601.prototype.IsoTimeFormat = ISO8601$IsoTimeFormat;
  ISO8601.prototype.IsoDateTimeFormat = ISO8601$IsoDateTimeFormat;
  Object.defineProperty(package$klock, 'ISO8601', {
    get: ISO8601_getInstance
  });
  package$klock.first_ki4uhp$ = first_0;
  package$klock.get_weekOfYear0_m2ds6$ = get_weekOfYear0;
  package$klock.get_weekOfYear1_m2ds6$ = get_weekOfYear1;
  package$klock.get_weekOfYear0_ksqbhc$ = get_weekOfYear0_0;
  package$klock.get_weekOfYear1_ksqbhc$ = get_weekOfYear1_0;
  Object.defineProperty(KlockLocale, 'Companion', {
    get: KlockLocale$Companion_getInstance
  });
  Object.defineProperty(KlockLocale$English, 'Companion', {
    get: KlockLocale$English$Companion_getInstance
  });
  KlockLocale.English = KlockLocale$English;
  package$klock.KlockLocale = KlockLocale;
  package$klock.get_microseconds_rcaex3$ = get_microseconds;
  package$klock.measureTime_o14v8n$ = measureTime;
  package$klock.measureTimeWithResult_9ce4rd$ = measureTimeWithResult;
  package$klock.TimedResult = TimedResult;
  Object.defineProperty(Month, 'January', {
    get: Month$January_getInstance
  });
  Object.defineProperty(Month, 'February', {
    get: Month$February_getInstance
  });
  Object.defineProperty(Month, 'March', {
    get: Month$March_getInstance
  });
  Object.defineProperty(Month, 'April', {
    get: Month$April_getInstance
  });
  Object.defineProperty(Month, 'May', {
    get: Month$May_getInstance
  });
  Object.defineProperty(Month, 'June', {
    get: Month$June_getInstance
  });
  Object.defineProperty(Month, 'July', {
    get: Month$July_getInstance
  });
  Object.defineProperty(Month, 'August', {
    get: Month$August_getInstance
  });
  Object.defineProperty(Month, 'September', {
    get: Month$September_getInstance
  });
  Object.defineProperty(Month, 'October', {
    get: Month$October_getInstance
  });
  Object.defineProperty(Month, 'November', {
    get: Month$November_getInstance
  });
  Object.defineProperty(Month, 'December', {
    get: Month$December_getInstance
  });
  Object.defineProperty(Month, 'Companion', {
    get: Month$Companion_getInstance
  });
  package$klock.Month = Month;
  package$klock.get_years_s8ev3n$ = get_years;
  package$klock.get_months_s8ev3n$ = get_months;
  package$klock.MonthSpan = MonthSpan;
  package$klock.get_totalYears_h7vlot$ = get_totalYears;
  package$klock.get_years_h7vlot$ = get_years_0;
  package$klock.get_months_h7vlot$ = get_months_0;
  Object.defineProperty(PatternDateFormat, 'Companion', {
    get: PatternDateFormat$Companion_getInstance
  });
  package$klock.PatternDateFormat_init_61zpoe$ = PatternDateFormat_init;
  package$klock.PatternDateFormat = PatternDateFormat;
  Object.defineProperty(package$klock, 'PerformanceCounter', {
    get: PerformanceCounter_getInstance
  });
  package$klock.TimeFormat = TimeFormat;
  package$klock.parse_5ggku9$ = parse_1;
  Object.defineProperty(TimeProvider, 'Companion', {
    get: TimeProvider$Companion_getInstance
  });
  package$klock.TimeProvider = TimeProvider;
  package$klock.TimeSpan = TimeSpan;
  package$klock.get_nanoseconds_rcaex3$ = get_nanoseconds;
  package$klock.get_milliseconds_rcaex3$ = get_milliseconds;
  package$klock.get_seconds_rcaex3$ = get_seconds;
  package$klock.get_minutes_rcaex3$ = get_minutes;
  package$klock.get_hours_rcaex3$ = get_hours;
  package$klock.get_days_rcaex3$ = get_days;
  package$klock.get_weeks_rcaex3$ = get_weeks;
  Object.defineProperty(TimeSpan, 'Companion', {
    get: TimeSpan$Companion_getInstance
  });
  package$klock.toTimeString_l8uqez$ = toTimeString;
  package$klock.max_3e7z32$ = max_0;
  package$klock.min_3e7z32$ = min_0;
  package$klock.clamp_izocc4$ = clamp_0;
  Object.defineProperty(TimezoneOffset, 'Companion', {
    get: TimezoneOffset$Companion_getInstance
  });
  package$klock.TimezoneOffset = TimezoneOffset;
  package$klock.get_offset_5bmjl6$ = get_offset;
  Object.defineProperty(Year, 'Companion', {
    get: Year$Companion_getInstance
  });
  package$klock.Year = Year;
  Object.defineProperty(YearMonth, 'Companion', {
    get: YearMonth$Companion_getInstance
  });
  package$klock.YearMonth = YearMonth;
  package$klock.withMonth_t7apbq$ = withMonth;
  package$klock.withYear_5uaifm$ = withYear;
  var package$internal = package$klock.internal || (package$klock.internal = {});
  package$internal.fastForEach_kf7q02$ = fastForEach;
  package$internal.MicroStrReader = MicroStrReader;
  package$internal.get_niceStr_1zw1ma$ = get_niceStr;
  Object.defineProperty(package$internal, 'MILLIS_PER_SECOND_8be2vx$', {
    get: function () {
      return MILLIS_PER_SECOND;
    }
  });
  Object.defineProperty(package$internal, 'MILLIS_PER_MINUTE_8be2vx$', {
    get: function () {
      return MILLIS_PER_MINUTE;
    }
  });
  Object.defineProperty(package$internal, 'MILLIS_PER_HOUR_8be2vx$', {
    get: function () {
      return MILLIS_PER_HOUR;
    }
  });
  Object.defineProperty(package$internal, 'MILLIS_PER_DAY_8be2vx$', {
    get: function () {
      return MILLIS_PER_DAY;
    }
  });
  Object.defineProperty(package$internal, 'MILLIS_PER_WEEK_8be2vx$', {
    get: function () {
      return MILLIS_PER_WEEK;
    }
  });
  package$internal.padded_b6l1hq$ = padded;
  package$internal.padded_f8d7mm$ = padded_0;
  package$internal.substr_tfrq3m$ = substr;
  package$internal.clamp_h8snvo$ = clamp_1;
  package$internal.cycle_h8snvo$ = cycle;
  package$internal.cycleSteps_h8snvo$ = cycleSteps;
  package$internal.splitKeep_g9pw2j$ = splitKeep;
  package$internal.umod_b6l1hq$ = umod;
  package$internal.Moduler = Moduler;
  package$internal.intDiv_3ahkds$ = intDiv;
  package$internal.toDateTime_t5kl13$ = toDateTime;
  package$internal.toDate_m2ds6$ = toDate;
  Object.defineProperty(package$internal, 'KlockInternal', {
    get: KlockInternal_getInstance
  });
  var package$js = package$klock.js || (package$klock.js = {});
  package$js.toDateTime_t5kl13$ = toDateTime_0;
  package$js.toDate_m2ds6$ = toDate_0;
  ISO8601$BaseIsoDateTimeFormat.prototype.tryParse_ivxn3r$ = DateFormat.prototype.tryParse_ivxn3r$;
  ISO8601$IsoDateTimeFormat.prototype.tryParse_ivxn3r$ = DateFormat.prototype.tryParse_ivxn3r$;
  ISO8601$DATE$ObjectLiteral.prototype.tryParse_ivxn3r$ = DateFormat.prototype.tryParse_ivxn3r$;
  PatternDateFormat.prototype.tryParse_ivxn3r$ = DateFormat.prototype.tryParse_ivxn3r$;
  KlockLocale_default = KlockLocale$English$Companion_getInstance();
  MILLIS_PER_SECOND = 1000;
  MILLIS_PER_MINUTE = 60000;
  MILLIS_PER_HOUR = 3600000;
  MILLIS_PER_DAY = 86400000;
  MILLIS_PER_WEEK = 604800000;
  isNode = equals(typeof window, 'undefined');
  initialHrTime = lazy(initialHrTime$lambda);
  Kotlin.defineModule('klock-root-klock', _);
  return _;
}));

//# sourceMappingURL=klock-root-klock.js.map
