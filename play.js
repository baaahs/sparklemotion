if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'play'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'play'.");
}
if (typeof this['kotlinx-coroutines-core'] === 'undefined') {
  throw new Error("Error loading module 'play'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'play'.");
}
var play = function (_, Kotlin, $module$kotlinx_coroutines_core) {
  'use strict';
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var throwUPAE = Kotlin.throwUPAE;
  var coroutines = $module$kotlinx_coroutines_core.kotlinx.coroutines;
  var Random = Kotlin.kotlin.random.Random;
  var delay = $module$kotlinx_coroutines_core.kotlinx.coroutines.delay_s8cxhz$;
  var Unit = Kotlin.kotlin.Unit;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var launch = $module$kotlinx_coroutines_core.kotlinx.coroutines.launch_s496o7$;
  var L60000 = Kotlin.Long.fromInt(60000);
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var math = Kotlin.kotlin.math;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var L200000 = Kotlin.Long.fromInt(200000);
  var L10000 = Kotlin.Long.fromInt(10000);
  var Pair = Kotlin.kotlin.Pair;
  var L1 = Kotlin.Long.ONE;
  var toString = Kotlin.kotlin.text.toString_dqglrj$;
  var L50 = Kotlin.Long.fromInt(50);
  var L0 = Kotlin.Long.ZERO;
  var ensureNotNull = Kotlin.ensureNotNull;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var toByte = Kotlin.toByte;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var toInt = Kotlin.kotlin.text.toInt_pdl1vz$;
  var toShort = Kotlin.toShort;
  var get_indices = Kotlin.kotlin.text.get_indices_gw00vp$;
  var copyOf = Kotlin.kotlin.collections.copyOf_mrm5p$;
  var toChar = Kotlin.toChar;
  var toBoxedChar = Kotlin.toBoxedChar;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var unboxChar = Kotlin.unboxChar;
  var promise = $module$kotlinx_coroutines_core.kotlinx.coroutines.promise_pda6u4$;
  var equals = Kotlin.equals;
  var clear = Kotlin.kotlin.dom.clear_asww5s$;
  var appendText = Kotlin.kotlin.dom.appendText_46n0ku$;
  var appendElement = Kotlin.kotlin.dom.appendElement_ldvnw0$;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  Cat.prototype = Object.create(Animal.prototype);
  Cat.prototype.constructor = Cat;
  Dog.prototype = Object.create(Animal.prototype);
  Dog.prototype.constructor = Dog;
  Type.prototype = Object.create(Enum.prototype);
  Type.prototype.constructor = Type;
  BrainHelloMessage.prototype = Object.create(Message.prototype);
  BrainHelloMessage.prototype.constructor = BrainHelloMessage;
  BrainShaderMessage.prototype = Object.create(Message.prototype);
  BrainShaderMessage.prototype.constructor = BrainShaderMessage;
  MapperHelloMessage.prototype = Object.create(Message.prototype);
  MapperHelloMessage.prototype.constructor = MapperHelloMessage;
  PinkyPongMessage.prototype = Object.create(Message.prototype);
  PinkyPongMessage.prototype.constructor = PinkyPongMessage;
  function Brain() {
  }
  Brain.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Brain',
    interfaces: []
  };
  function SimBrain(network, display, jsPanel) {
    this.network_0 = network;
    this.display_0 = display;
    this.jsPanel_0 = jsPanel;
    this.link_sq3vvv$_0 = this.link_sq3vvv$_0;
    this.receivingInstructions_0 = false;
  }
  Object.defineProperty(SimBrain.prototype, 'link_0', {
    get: function () {
      if (this.link_sq3vvv$_0 == null)
        return throwUPAE('link');
      return this.link_sq3vvv$_0;
    },
    set: function (link) {
      this.link_sq3vvv$_0 = link;
    }
  });
  function Coroutine$SimBrain$start$lambda(this$SimBrain_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$SimBrain = this$SimBrain_0;
  }
  Coroutine$SimBrain$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SimBrain$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SimBrain$start$lambda.prototype.constructor = Coroutine$SimBrain$start$lambda;
  Coroutine$SimBrain$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var timeMillis = Random.Default.nextInt() % 1000;
            this.state_0 = 2;
            this.result_0 = delay(Kotlin.Long.fromInt(timeMillis), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$this$SimBrain.run(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function SimBrain$start$lambda(this$SimBrain_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SimBrain$start$lambda(this$SimBrain_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  SimBrain.prototype.start = function () {
    launch(coroutines.GlobalScope, void 0, void 0, SimBrain$start$lambda(this));
  };
  function Coroutine$run($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run.prototype.constructor = Coroutine$run;
  Coroutine$run.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.link_0 = this.$this.network_0.link();
            this.$this.link_0.listen_nmsgsy$(Ports$Companion_getInstance().BRAIN, this.$this);
            this.$this.display_0.haveLink_6qu7we$(this.$this.link_0);
            this.$this.jsPanel_0.select();
            this.state_0 = 2;
            this.result_0 = this.$this.sendHello_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  SimBrain.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$sendHello_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$sendHello_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$sendHello_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$sendHello_0.prototype.constructor = Coroutine$sendHello_0;
  Coroutine$sendHello_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.$this.receivingInstructions_0) {
              this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new BrainHelloMessage());
            }

            this.state_0 = 3;
            this.result_0 = delay(L60000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  SimBrain.prototype.sendHello_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$sendHello_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  SimBrain.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainShaderMessage))
      this.jsPanel_0.color = message.color;
  };
  SimBrain.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimBrain',
    interfaces: [Network$Listener, Brain]
  };
  function Display() {
  }
  Display.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Display',
    interfaces: []
  };
  function NetworkDisplay() {
  }
  NetworkDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'NetworkDisplay',
    interfaces: []
  };
  function PinkyDisplay() {
  }
  PinkyDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'PinkyDisplay',
    interfaces: []
  };
  function BrainDisplay() {
  }
  BrainDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'BrainDisplay',
    interfaces: []
  };
  function MapperDisplay() {
  }
  MapperDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MapperDisplay',
    interfaces: []
  };
  function ThingWithMass() {
  }
  ThingWithMass.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ThingWithMass',
    interfaces: []
  };
  function Animal(age) {
    this.age = age;
  }
  Animal.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Animal',
    interfaces: []
  };
  function Cat(age) {
    Animal.call(this, age);
  }
  Cat.prototype.weightInKilograms = function () {
    return 2.0 * this.age;
  };
  Cat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Cat',
    interfaces: [ThingWithMass, Animal]
  };
  function Dog(age) {
    Animal.call(this, age);
  }
  Dog.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Dog',
    interfaces: [Animal]
  };
  function Glass(height, diameter, fullness) {
    this.height = height;
    this.diameter = diameter;
    this.fullness = fullness;
  }
  Glass.prototype.weightInKilograms = function () {
    return this.computeVolume() / 1000;
  };
  Glass.prototype.computeVolume = function () {
    return this.diameter / 2 * (this.diameter / 2) * math.PI * this.height;
  };
  Glass.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Glass',
    interfaces: [ThingWithMass]
  };
  function kevmoMain() {
    new Cat(3);
    var kitty = new Cat(5);
    var myGlass = new Glass(3.5, 4.0, 0.3);
    println('volume of the glass is ' + myGlass.computeVolume());
    var yourglass = new Glass(6.0, 12.0, 0.7);
    println('your glass contains ' + yourglass.computeVolume() + ' milliliters');
    var totalWeight = kitty.weightInKilograms() + myGlass.weightInKilograms() + yourglass.weightInKilograms();
    println('The weightInKilograms of our cats and glasses is: ' + totalWeight);
  }
  var main;
  function get_main() {
    if (main == null)
      return throwUPAE('main');
    return main;
  }
  function set_main(main_0) {
    main = main_0;
  }
  function Main() {
    this.display = getDisplay();
    this.network = new FakeNetwork(void 0, this.display.forNetwork());
    this.sheepModel = new SheepModel();
    this.pinky = new Pinky(this.network, this.display.forPinky());
    this.mapper = new Mapper(this.network, this.display.forMapper());
  }
  function Coroutine$Main$start$lambda(continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
  }
  Coroutine$Main$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Main$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Main$start$lambda.prototype.constructor = Coroutine$Main$start$lambda;
  Coroutine$Main$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = delay(L200000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Main$start$lambda(continuation_0, suspended) {
    var instance = new Coroutine$Main$start$lambda(continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  Main.prototype.start = function () {
    this.sheepModel.load();
    this.mapper.start();
    this.pinky.start();
    initThreeJs(this.sheepModel);
    var tmp$;
    tmp$ = this.sheepModel.panels.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var jsPanelObj = addPanel(element);
      (new SimBrain(this.network, this.display.forBrain(), new JsPanel(jsPanelObj))).start();
    }
    startRender();
    doRunBlocking(Main$start$lambda);
  };
  Main.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Main',
    interfaces: []
  };
  function JsPanel(jsPanelObj) {
    this.jsPanelObj_0 = jsPanelObj;
    this.color_1o5p8y$_0 = Color$Companion_getInstance().BLACK;
  }
  Object.defineProperty(JsPanel.prototype, 'color', {
    get: function () {
      return this.color_1o5p8y$_0;
    },
    set: function (value) {
      setPanelColor(this.jsPanelObj_0, value);
      this.color_1o5p8y$_0 = this.color;
    }
  });
  JsPanel.prototype.select = function () {
    selectPanel(this.jsPanelObj_0, true);
  };
  JsPanel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsPanel',
    interfaces: []
  };
  function Mapper(network, display) {
    this.network = network;
    this.display = display;
    this.link_tktc8n$_0 = this.link_tktc8n$_0;
    this.isRunning_0 = false;
  }
  Object.defineProperty(Mapper.prototype, 'link_0', {
    get: function () {
      if (this.link_tktc8n$_0 == null)
        return throwUPAE('link');
      return this.link_tktc8n$_0;
    },
    set: function (link) {
      this.link_tktc8n$_0 = link;
    }
  });
  function Coroutine$Mapper$start$lambda$lambda(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$start$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$start$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$start$lambda$lambda.prototype.constructor = Coroutine$Mapper$start$lambda$lambda;
  Coroutine$Mapper$start$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$Mapper.run(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$start$lambda$lambda(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$start$lambda$lambda(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Mapper$start$lambda(this$Mapper) {
    return function () {
      if (!this$Mapper.isRunning_0) {
        this$Mapper.isRunning_0 = true;
        launch(coroutines.GlobalScope, void 0, void 0, Mapper$start$lambda$lambda(this$Mapper));
      }
      return Unit;
    };
  }
  function Mapper$start$lambda_0(this$Mapper) {
    return function () {
      if (this$Mapper.isRunning_0) {
        this$Mapper.isRunning_0 = false;
      }
      return Unit;
    };
  }
  Mapper.prototype.start = function () {
    this.display.onStart = Mapper$start$lambda(this);
    this.display.onStop = Mapper$start$lambda_0(this);
  };
  function Coroutine$run_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run_0.prototype.constructor = Coroutine$run_0;
  Coroutine$run_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.link_0 = this.$this.network.link();
            this.$this.link_0.listen_nmsgsy$(Ports$Companion_getInstance().MAPPER, this.$this);
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.$this.isRunning_0) {
              this.state_0 = 4;
              continue;
            }

            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
            this.state_0 = 3;
            this.result_0 = delay(L10000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Mapper.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, PinkyPongMessage)) {
      println('Mapper: pong from pinky: ' + message.brainIds);
      var tmp$;
      tmp$ = message.brainIds.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        println('id = ' + element);
      }
    }
  };
  Mapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mapper',
    interfaces: [Network$Listener]
  };
  function Network() {
  }
  function Network$Link() {
  }
  Network$Link.prototype.send_bkw8fl$ = function (toAddress, port, message) {
    this.send_z62edq$(toAddress, port, message.toBytes());
  };
  Network$Link.prototype.broadcast_ecsl0t$ = function (port, message) {
    this.broadcast_3fbn1q$(port, message.toBytes());
  };
  Network$Link.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Link',
    interfaces: []
  };
  function Network$Address() {
  }
  Network$Address.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Address',
    interfaces: []
  };
  function Network$Listener() {
  }
  Network$Listener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Listener',
    interfaces: []
  };
  Network.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Network',
    interfaces: []
  };
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  function FakeNetwork(networkDelay, display) {
    if (networkDelay === void 0)
      networkDelay = L1;
    this.networkDelay_0 = networkDelay;
    this.display_0 = display;
    this.listeners_0 = HashMap_init();
    this.listenersByPort_0 = HashMap_init();
    this.nextAddress_0 = 45071;
  }
  FakeNetwork.prototype.link = function () {
    var tmp$;
    var address = new FakeAddress((tmp$ = this.nextAddress_0, this.nextAddress_0 = tmp$ + 1 | 0, tmp$));
    return new FakeNetwork$FakeLink(this, address);
  };
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  FakeNetwork.prototype.listen_0 = function (address, port, listener) {
    this.listeners_0.put_xwzc9p$(new Pair(address, port), listener);
    var $receiver = this.listenersByPort_0;
    var tmp$;
    var value = $receiver.get_11rb$(port);
    if (value == null) {
      var answer = ArrayList_init();
      $receiver.put_xwzc9p$(port, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    var portListeners = tmp$;
    portListeners.add_11rb$(listener);
  };
  FakeNetwork.prototype.send_0 = function (fromAddress, toAddress, port, bytes) {
    var listener = this.listeners_0.get_11rb$(new Pair(toAddress, port));
    if (listener != null)
      this.transmit_0(fromAddress, listener, bytes);
  };
  FakeNetwork.prototype.broadcast_0 = function (fromAddress, port, bytes) {
    var tmp$;
    if ((tmp$ = this.listenersByPort_0.get_11rb$(port)) != null) {
      var tmp$_0;
      tmp$_0 = tmp$.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        this.transmit_0(fromAddress, element, bytes);
      }
    }
  };
  function Coroutine$FakeNetwork$transmit$lambda(this$FakeNetwork_0, closure$listener_0, closure$fromAddress_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$listener = closure$listener_0;
    this.local$closure$fromAddress = closure$fromAddress_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$FakeNetwork$transmit$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$transmit$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$transmit$lambda.prototype.constructor = Coroutine$FakeNetwork$transmit$lambda;
  Coroutine$FakeNetwork$transmit$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = delay(this.local$this$FakeNetwork.networkDelay_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (Random.Default.nextInt() % 10 !== 1) {
              this.local$this$FakeNetwork.display_0.receivedPacket();
              return this.local$closure$listener.receive_cm0rz4$(this.local$closure$fromAddress, this.local$closure$bytes), Unit;
            }
             else {
              return this.local$this$FakeNetwork.display_0.droppedPacket(), Unit;
            }

          case 3:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function FakeNetwork$transmit$lambda(this$FakeNetwork_0, closure$listener_0, closure$fromAddress_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$transmit$lambda(this$FakeNetwork_0, closure$listener_0, closure$fromAddress_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork.prototype.transmit_0 = function (fromAddress, listener, bytes) {
    launch(coroutines.GlobalScope, void 0, void 0, FakeNetwork$transmit$lambda(this, listener, fromAddress, bytes));
  };
  function FakeNetwork$FakeLink($outer, myAddress) {
    this.$outer = $outer;
    this.myAddress_1ma6oo$_0 = myAddress;
  }
  Object.defineProperty(FakeNetwork$FakeLink.prototype, 'myAddress', {
    get: function () {
      return this.myAddress_1ma6oo$_0;
    }
  });
  FakeNetwork$FakeLink.prototype.listen_nmsgsy$ = function (port, listener) {
    this.$outer.listen_0(this.myAddress, port, listener);
  };
  FakeNetwork$FakeLink.prototype.send_z62edq$ = function (toAddress, port, bytes) {
    this.$outer.send_0(this.myAddress, toAddress, port, bytes);
  };
  FakeNetwork$FakeLink.prototype.broadcast_3fbn1q$ = function (port, bytes) {
    this.$outer.broadcast_0(this.myAddress, port, bytes);
  };
  FakeNetwork$FakeLink.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeLink',
    interfaces: [Network$Link]
  };
  FakeNetwork.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeNetwork',
    interfaces: [Network]
  };
  function FakeAddress(id) {
    this.id = id;
  }
  FakeAddress.prototype.toString = function () {
    return 'x' + toString(this.id, 16);
  };
  FakeAddress.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeAddress',
    interfaces: [Network$Address]
  };
  FakeAddress.prototype.component1 = function () {
    return this.id;
  };
  FakeAddress.prototype.copy_za3lpa$ = function (id) {
    return new FakeAddress(id === void 0 ? this.id : id);
  };
  FakeAddress.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.id) | 0;
    return result;
  };
  FakeAddress.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.id, other.id))));
  };
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  function Pinky(network, display) {
    this.network = network;
    this.display = display;
    this.link_e4s3v3$_0 = this.link_e4s3v3$_0;
    this.brains_0 = LinkedHashMap_init();
    this.beatProvider_0 = new Pinky$BeatProvider(this, 120.0);
    this.show_0 = new SomeDumbShow();
    this.mapperIsRunning_0 = false;
  }
  Object.defineProperty(Pinky.prototype, 'link_0', {
    get: function () {
      if (this.link_e4s3v3$_0 == null)
        return throwUPAE('link');
      return this.link_e4s3v3$_0;
    },
    set: function (link) {
      this.link_e4s3v3$_0 = link;
    }
  });
  Pinky.prototype.run = function () {
    this.link_0 = this.network.link();
    this.link_0.listen_nmsgsy$(Ports$Companion_getInstance().PINKY, this);
  };
  function Coroutine$Pinky$start$lambda(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$start$lambda.prototype.constructor = Coroutine$Pinky$start$lambda;
  Coroutine$Pinky$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$this$Pinky.run(), Unit;
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Pinky$start$lambda(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$start$lambda(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Pinky$start$lambda_0(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$start$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$start$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$start$lambda_0.prototype.constructor = Coroutine$Pinky$start$lambda_0;
  Coroutine$Pinky$start$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$Pinky.beatProvider_0.run(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Pinky$start$lambda_0(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$start$lambda_0(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Pinky$start$lambda_1(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$start$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$start$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$start$lambda_1.prototype.constructor = Coroutine$Pinky$start$lambda_1;
  Coroutine$Pinky$start$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$this$Pinky.mapperIsRunning_0) {
              this.local$this$Pinky.show_0.nextFrame_n2m8bc$(this.local$this$Pinky.brains_0, this.local$this$Pinky.link_0);
            }

            this.state_0 = 3;
            this.result_0 = delay(L50, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Pinky$start$lambda_1(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$start$lambda_1(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  Pinky.prototype.start = function () {
    launch(coroutines.GlobalScope, void 0, void 0, Pinky$start$lambda(this));
    launch(coroutines.GlobalScope, void 0, void 0, Pinky$start$lambda_0(this));
    launch(coroutines.GlobalScope, void 0, void 0, Pinky$start$lambda_1(this));
  };
  Pinky.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainHelloMessage))
      this.foundBrain_0(new RemoteBrain(fromAddress));
    else if (Kotlin.isType(message, MapperHelloMessage))
      this.mapperIsRunning_0 = message.isRunning;
  };
  Pinky.prototype.foundBrain_0 = function (remoteBrain) {
    this.brains_0.put_xwzc9p$(remoteBrain.address, remoteBrain);
    this.display.brainCount = this.brains_0.size;
  };
  function Pinky$BeatProvider($outer, bpm) {
    this.$outer = $outer;
    this.bpm = bpm;
    this.startTimeMillis = L0;
    this.beat = 0;
    this.beatsPerMeasure = 4;
  }
  function Coroutine$run_1($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run_1.prototype.constructor = Coroutine$run_1;
  Coroutine$run_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.startTimeMillis = getTimeMillis();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.$this.$outer.display.beat = this.$this.beat;
            var offsetMillis = getTimeMillis().subtract(this.$this.startTimeMillis);
            var millisPerBeat = Kotlin.Long.fromNumber(1000 / (this.$this.bpm / 60));
            var delayTimeMillis = millisPerBeat.subtract(offsetMillis.modulo(millisPerBeat));
            this.state_0 = 3;
            this.result_0 = delay(delayTimeMillis, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.$this.beat = (this.$this.beat + 1 | 0) % this.$this.beatsPerMeasure;
            this.state_0 = 2;
            continue;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Pinky$BeatProvider.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_1(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Pinky$BeatProvider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BeatProvider',
    interfaces: []
  };
  Pinky.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Pinky',
    interfaces: [Network$Listener]
  };
  function RemoteBrain(address) {
    this.address = address;
  }
  RemoteBrain.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemoteBrain',
    interfaces: []
  };
  function SomeDumbShow() {
    this.priorBrain = null;
  }
  var random = Kotlin.kotlin.collections.random_iscd7z$;
  SomeDumbShow.prototype.nextFrame_n2m8bc$ = function (brains, link) {
    if (this.priorBrain != null) {
      link.send_bkw8fl$(ensureNotNull(this.priorBrain).address, Ports$Companion_getInstance().BRAIN, new BrainShaderMessage(Color$Companion_getInstance().BLACK));
    }
    if (!brains.isEmpty()) {
      var highlightBrain = random(brains.values, Random.Default);
      link.send_bkw8fl$(highlightBrain.address, Ports$Companion_getInstance().BRAIN, new BrainShaderMessage(Color$Companion_getInstance().random()));
    }
  };
  SomeDumbShow.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SomeDumbShow',
    interfaces: []
  };
  function Ports() {
    Ports$Companion_getInstance();
  }
  function Ports$Companion() {
    Ports$Companion_instance = this;
    this.MAPPER = 8001;
    this.PINKY = 8002;
    this.BRAIN = 8003;
  }
  Ports$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Ports$Companion_instance = null;
  function Ports$Companion_getInstance() {
    if (Ports$Companion_instance === null) {
      new Ports$Companion();
    }
    return Ports$Companion_instance;
  }
  Ports.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Ports',
    interfaces: []
  };
  function Type(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function Type_initFields() {
    Type_initFields = function () {
    };
    Type$BRAIN_HELLO_instance = new Type('BRAIN_HELLO', 0);
    Type$BRAIN_PANEL_SHADE_instance = new Type('BRAIN_PANEL_SHADE', 1);
    Type$MAPPER_HELLO_instance = new Type('MAPPER_HELLO', 2);
    Type$PINKY_PONG_instance = new Type('PINKY_PONG', 3);
  }
  var Type$BRAIN_HELLO_instance;
  function Type$BRAIN_HELLO_getInstance() {
    Type_initFields();
    return Type$BRAIN_HELLO_instance;
  }
  var Type$BRAIN_PANEL_SHADE_instance;
  function Type$BRAIN_PANEL_SHADE_getInstance() {
    Type_initFields();
    return Type$BRAIN_PANEL_SHADE_instance;
  }
  var Type$MAPPER_HELLO_instance;
  function Type$MAPPER_HELLO_getInstance() {
    Type_initFields();
    return Type$MAPPER_HELLO_instance;
  }
  var Type$PINKY_PONG_instance;
  function Type$PINKY_PONG_getInstance() {
    Type_initFields();
    return Type$PINKY_PONG_instance;
  }
  Type.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Type',
    interfaces: [Enum]
  };
  function Type$values() {
    return [Type$BRAIN_HELLO_getInstance(), Type$BRAIN_PANEL_SHADE_getInstance(), Type$MAPPER_HELLO_getInstance(), Type$PINKY_PONG_getInstance()];
  }
  Type.values = Type$values;
  function Type$valueOf(name) {
    switch (name) {
      case 'BRAIN_HELLO':
        return Type$BRAIN_HELLO_getInstance();
      case 'BRAIN_PANEL_SHADE':
        return Type$BRAIN_PANEL_SHADE_getInstance();
      case 'MAPPER_HELLO':
        return Type$MAPPER_HELLO_getInstance();
      case 'PINKY_PONG':
        return Type$PINKY_PONG_getInstance();
      default:throwISE('No enum constant baaahs.Type.' + name);
    }
  }
  Type.valueOf_61zpoe$ = Type$valueOf;
  function parse(bytes) {
    var tmp$;
    var reader = new ByteArrayReader(bytes);
    switch (Type$values()[reader.readByte()].name) {
      case 'BRAIN_HELLO':
        tmp$ = new BrainHelloMessage();
        break;
      case 'BRAIN_PANEL_SHADE':
        tmp$ = BrainShaderMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'MAPPER_HELLO':
        tmp$ = MapperHelloMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'PINKY_PONG':
        tmp$ = PinkyPongMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  }
  function BrainHelloMessage() {
    Message.call(this, Type$BRAIN_HELLO_getInstance());
  }
  BrainHelloMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainHelloMessage',
    interfaces: [Message]
  };
  function BrainShaderMessage(color) {
    BrainShaderMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_PANEL_SHADE_getInstance());
    this.color = color;
  }
  function BrainShaderMessage$Companion() {
    BrainShaderMessage$Companion_instance = this;
  }
  BrainShaderMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
    return new BrainShaderMessage(Color$Companion_getInstance().parse_c4pr8w$(reader));
  };
  BrainShaderMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainShaderMessage$Companion_instance = null;
  function BrainShaderMessage$Companion_getInstance() {
    if (BrainShaderMessage$Companion_instance === null) {
      new BrainShaderMessage$Companion();
    }
    return BrainShaderMessage$Companion_instance;
  }
  BrainShaderMessage.prototype.serialize_ep8mow$ = function (writer) {
    this.color.serialize_ep8mow$(writer);
  };
  BrainShaderMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainShaderMessage',
    interfaces: [Message]
  };
  function MapperHelloMessage(isRunning) {
    MapperHelloMessage$Companion_getInstance();
    Message.call(this, Type$MAPPER_HELLO_getInstance());
    this.isRunning = isRunning;
  }
  function MapperHelloMessage$Companion() {
    MapperHelloMessage$Companion_instance = this;
  }
  MapperHelloMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
    return new MapperHelloMessage(reader.readBoolean());
  };
  MapperHelloMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MapperHelloMessage$Companion_instance = null;
  function MapperHelloMessage$Companion_getInstance() {
    if (MapperHelloMessage$Companion_instance === null) {
      new MapperHelloMessage$Companion();
    }
    return MapperHelloMessage$Companion_instance;
  }
  MapperHelloMessage.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeBoolean_6taknv$(this.isRunning);
  };
  MapperHelloMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapperHelloMessage',
    interfaces: [Message]
  };
  function PinkyPongMessage(brainIds) {
    PinkyPongMessage$Companion_getInstance();
    Message.call(this, Type$PINKY_PONG_getInstance());
    this.brainIds = brainIds;
  }
  function PinkyPongMessage$Companion() {
    PinkyPongMessage$Companion_instance = this;
  }
  PinkyPongMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var brainCount = reader.readInt();
    var brainIds = ArrayList_init();
    for (var i = 0; i < brainCount; i++) {
      brainIds.add_11rb$(reader.readString());
    }
    return new PinkyPongMessage(brainIds);
  };
  PinkyPongMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PinkyPongMessage$Companion_instance = null;
  function PinkyPongMessage$Companion_getInstance() {
    if (PinkyPongMessage$Companion_instance === null) {
      new PinkyPongMessage$Companion();
    }
    return PinkyPongMessage$Companion_instance;
  }
  PinkyPongMessage.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeInt_za3lpa$(this.brainIds.size);
    var tmp$;
    tmp$ = this.brainIds.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      writer.writeString_61zpoe$(element);
    }
  };
  PinkyPongMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PinkyPongMessage',
    interfaces: [Message]
  };
  function Message(type) {
    this.type = type;
  }
  Message.prototype.toBytes = function () {
    var writer = ByteArrayWriter_init(1 + this.size() | 0);
    writer.writeByte_s8j3t7$(toByte(this.type.ordinal));
    this.serialize_ep8mow$(writer);
    return writer.toBytes();
  };
  Message.prototype.serialize_ep8mow$ = function (writer) {
  };
  Message.prototype.size = function () {
    return 127;
  };
  Message.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Message',
    interfaces: []
  };
  function SheepModel() {
    this.vertices_mqvov9$_0 = this.vertices_mqvov9$_0;
    this.panels_kixrwx$_0 = this.panels_kixrwx$_0;
  }
  Object.defineProperty(SheepModel.prototype, 'vertices', {
    get: function () {
      if (this.vertices_mqvov9$_0 == null)
        return throwUPAE('vertices');
      return this.vertices_mqvov9$_0;
    },
    set: function (vertices) {
      this.vertices_mqvov9$_0 = vertices;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'panels', {
    get: function () {
      if (this.panels_kixrwx$_0 == null)
        return throwUPAE('panels');
      return this.panels_kixrwx$_0;
    },
    set: function (panels) {
      this.panels_kixrwx$_0 = panels;
    }
  });
  var throwCCE = Kotlin.throwCCE;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  SheepModel.prototype.load = function () {
    var vertices = ArrayList_init();
    var panels = ArrayList_init();
    var currentPanel = {v: new SheepModel$Panel('initial')};
    var $receiver = split(getResource('newsheep_processed.obj'), ['\n']);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0;
      destination.add_11rb$(trim(Kotlin.isCharSequence(tmp$_0 = item) ? tmp$_0 : throwCCE()).toString());
    }
    var tmp$_1;
    tmp$_1 = destination.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      var tmp$_2;
      var parts = split(element, [' ']);
      var args = parts.subList_vux9f0$(1, parts.size);
      switch (parts.get_za3lpa$(0)) {
        case 'v':
          if (args.size !== 3)
            throw Exception_init('invalid vertex line: ' + element);
          var destination_0 = ArrayList_init_0(collectionSizeOrDefault(args, 10));
          var tmp$_3;
          tmp$_3 = args.iterator();
          while (tmp$_3.hasNext()) {
            var item_0 = tmp$_3.next();
            destination_0.add_11rb$(toDouble(item_0));
          }

          var coords = destination_0;
          vertices.add_11rb$(new SheepModel$Point(coords.get_za3lpa$(0), coords.get_za3lpa$(1), coords.get_za3lpa$(2)));
          break;
        case 'g':
          var name = joinToString(args, ' ');
          var match = Regex_init('^G_([^_]+).*?$').matchEntire_6bul2c$(name);
          if (match != null) {
            name = ensureNotNull(match.groups.get_za3lpa$(1)).value;
          }

          currentPanel.v = new SheepModel$Panel(name);
          panels.add_11rb$(currentPanel.v);
          break;
        case 'f':
          var destination_1 = ArrayList_init_0(collectionSizeOrDefault(args, 10));
          var tmp$_4;
          tmp$_4 = args.iterator();
          while (tmp$_4.hasNext()) {
            var item_1 = tmp$_4.next();
            destination_1.add_11rb$(toInt(item_1) - 1 | 0);
          }

          var verts = destination_1;
          currentPanel.v.faces.faces.add_11rb$(new SheepModel$Face(verts));
          break;
        case 'l':
          var destination_2 = ArrayList_init_0(collectionSizeOrDefault(args, 10));
          var tmp$_5;
          tmp$_5 = args.iterator();
          while (tmp$_5.hasNext()) {
            var item_2 = tmp$_5.next();
            destination_2.add_11rb$(toInt(item_2) - 1 | 0);
          }

          var verts_0 = destination_2;
          var points = ArrayList_init();
          tmp$_2 = verts_0.iterator();
          while (tmp$_2.hasNext()) {
            var vi = tmp$_2.next();
            var v = vertices.get_za3lpa$(vi);
            points.add_11rb$(v);
          }

          currentPanel.v.lines.add_11rb$(new SheepModel$Line(points));
          break;
      }
    }
    println('Sheep model has ' + panels.size + ' panels (and ' + vertices.size + ' vertices)!');
    this.vertices = vertices;
    this.panels = panels;
  };
  function SheepModel$Point(x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  SheepModel$Point.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Point',
    interfaces: []
  };
  SheepModel$Point.prototype.component1 = function () {
    return this.x;
  };
  SheepModel$Point.prototype.component2 = function () {
    return this.y;
  };
  SheepModel$Point.prototype.component3 = function () {
    return this.z;
  };
  SheepModel$Point.prototype.copy_y2kzbl$ = function (x, y, z) {
    return new SheepModel$Point(x === void 0 ? this.x : x, y === void 0 ? this.y : y, z === void 0 ? this.z : z);
  };
  SheepModel$Point.prototype.toString = function () {
    return 'Point(x=' + Kotlin.toString(this.x) + (', y=' + Kotlin.toString(this.y)) + (', z=' + Kotlin.toString(this.z)) + ')';
  };
  SheepModel$Point.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.z) | 0;
    return result;
  };
  SheepModel$Point.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.z, other.z)))));
  };
  function SheepModel$Line(points) {
    this.points = points;
  }
  SheepModel$Line.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Line',
    interfaces: []
  };
  SheepModel$Line.prototype.component1 = function () {
    return this.points;
  };
  SheepModel$Line.prototype.copy_5otmf7$ = function (points) {
    return new SheepModel$Line(points === void 0 ? this.points : points);
  };
  SheepModel$Line.prototype.toString = function () {
    return 'Line(points=' + Kotlin.toString(this.points) + ')';
  };
  SheepModel$Line.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.points) | 0;
    return result;
  };
  SheepModel$Line.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.points, other.points))));
  };
  function SheepModel$Face(vertexIds) {
    this.vertexIds = vertexIds;
  }
  SheepModel$Face.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Face',
    interfaces: []
  };
  function SheepModel$Faces() {
    this.vertices = ArrayList_init();
    this.faces = ArrayList_init();
  }
  SheepModel$Faces.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Faces',
    interfaces: []
  };
  function SheepModel$Panel(name) {
    this.name = name;
    this.faces = new SheepModel$Faces();
    this.lines = ArrayList_init();
  }
  SheepModel$Panel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Panel',
    interfaces: []
  };
  SheepModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SheepModel',
    interfaces: []
  };
  function Color(red, green, blue) {
    Color$Companion_getInstance();
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
  Color.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeByte_s8j3t7$(toByte(this.red & 255));
    writer.writeByte_s8j3t7$(toByte(this.green & 255));
    writer.writeByte_s8j3t7$(toByte(this.blue & 255));
  };
  Color.prototype.toInt = function () {
    return this.red << 16 & 16711680 | this.green << 8 & 65280 | this.blue & 255;
  };
  function Color$Companion() {
    Color$Companion_instance = this;
    this.BLACK = new Color(0, 0, 0);
    this.WHITE = new Color(-128, -128, -128);
  }
  Color$Companion.prototype.random = function () {
    return new Color(Random.Default.nextInt() & 255, Random.Default.nextInt() & 255, Random.Default.nextInt() & 255);
  };
  Color$Companion.prototype.parse_c4pr8w$ = function (reader) {
    return new Color(reader.readByte() & 255, reader.readByte() & 255, reader.readByte() & 255);
  };
  Color$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Color$Companion_instance = null;
  function Color$Companion_getInstance() {
    if (Color$Companion_instance === null) {
      new Color$Companion();
    }
    return Color$Companion_instance;
  }
  Color.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Color',
    interfaces: []
  };
  Color.prototype.component1 = function () {
    return this.red;
  };
  Color.prototype.component2 = function () {
    return this.green;
  };
  Color.prototype.component3 = function () {
    return this.blue;
  };
  Color.prototype.copy_qt1dr2$ = function (red, green, blue) {
    return new Color(red === void 0 ? this.red : red, green === void 0 ? this.green : green, blue === void 0 ? this.blue : blue);
  };
  Color.prototype.toString = function () {
    return 'Color(red=' + Kotlin.toString(this.red) + (', green=' + Kotlin.toString(this.green)) + (', blue=' + Kotlin.toString(this.blue)) + ')';
  };
  Color.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.red) | 0;
    result = result * 31 + Kotlin.hashCode(this.green) | 0;
    result = result * 31 + Kotlin.hashCode(this.blue) | 0;
    return result;
  };
  Color.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.red, other.red) && Kotlin.equals(this.green, other.green) && Kotlin.equals(this.blue, other.blue)))));
  };
  function ByteArrayWriter(bytes, offset) {
    if (bytes === void 0)
      bytes = new Int8Array(128);
    if (offset === void 0)
      offset = 0;
    this.bytes_0 = bytes;
    this.offset = offset;
  }
  ByteArrayWriter.prototype.writeBoolean_6taknv$ = function (b) {
    var tmp$;
    this.growIfNecessary_0(1);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = b ? 1 : 0;
  };
  ByteArrayWriter.prototype.writeByte_s8j3t7$ = function (b) {
    var tmp$;
    this.growIfNecessary_0(1);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = b;
  };
  ByteArrayWriter.prototype.writeShort_mq22fl$ = function (s) {
    var tmp$, tmp$_0;
    this.growIfNecessary_0(2);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = toByte(s >> 8 & 255);
    this.bytes_0[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] = toByte(s & 255);
  };
  ByteArrayWriter.prototype.writeChar_s8itvh$ = function (c) {
    this.writeShort_mq22fl$(toShort(c | 0));
  };
  ByteArrayWriter.prototype.writeInt_za3lpa$ = function (l) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.growIfNecessary_0(4);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = toByte(l >> 24 & 255);
    this.bytes_0[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] = toByte(l >> 16 & 255);
    this.bytes_0[tmp$_1 = this.offset, this.offset = tmp$_1 + 1 | 0, tmp$_1] = toByte(l >> 8 & 255);
    this.bytes_0[tmp$_2 = this.offset, this.offset = tmp$_2 + 1 | 0, tmp$_2] = toByte(l & 255);
  };
  ByteArrayWriter.prototype.writeString_61zpoe$ = function (s) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.growIfNecessary_0(4 + (2 * s.length | 0) | 0);
    this.writeInt_za3lpa$(s.length);
    tmp$ = get_indices(s);
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
      this.writeChar_s8itvh$(s.charCodeAt(i));
    }
  };
  ByteArrayWriter.prototype.toBytes = function () {
    return copyOf(this.bytes_0, this.offset);
  };
  ByteArrayWriter.prototype.growIfNecessary_0 = function (by) {
    if ((this.offset + by | 0) >= this.bytes_0.length) {
      this.bytes_0 = copyOf(this.bytes_0, this.bytes_0.length * 2 | 0);
    }
  };
  ByteArrayWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayWriter',
    interfaces: []
  };
  function ByteArrayWriter_init(size, $this) {
    $this = $this || Object.create(ByteArrayWriter.prototype);
    ByteArrayWriter.call($this, new Int8Array(size));
    return $this;
  }
  function ByteArrayReader(bytes, offset) {
    if (offset === void 0)
      offset = 0;
    this.bytes = bytes;
    this.offset = offset;
  }
  ByteArrayReader.prototype.readBoolean = function () {
    return this.bytes[this.offset] !== toByte(0);
  };
  ByteArrayReader.prototype.readByte = function () {
    var tmp$;
    return this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$];
  };
  ByteArrayReader.prototype.readShort = function () {
    var tmp$, tmp$_0;
    return toShort((this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] & 255) << 8 | this.bytes[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] & 255);
  };
  ByteArrayReader.prototype.readChar = function () {
    return toBoxedChar(toChar(this.readShort()));
  };
  ByteArrayReader.prototype.readInt = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    return (this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] & 255) << 24 | (this.bytes[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] & 255) << 16 | (this.bytes[tmp$_1 = this.offset, this.offset = tmp$_1 + 1 | 0, tmp$_1] & 255) << 8 | this.bytes[tmp$_2 = this.offset, this.offset = tmp$_2 + 1 | 0, tmp$_2] & 255;
  };
  ByteArrayReader.prototype.readString = function () {
    var length = this.readInt();
    var buf = StringBuilder_init(length);
    for (var i = 0; i < length; i++) {
      buf.append_s8itvh$(unboxChar(this.readChar()));
    }
    return buf.toString();
  };
  ByteArrayReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayReader',
    interfaces: []
  };
  function hello() {
    return 'Hello from JS';
  }
  function Coroutine$doRunBlocking$lambda(closure$block_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$block = closure$block_0;
  }
  Coroutine$doRunBlocking$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$doRunBlocking$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$doRunBlocking$lambda.prototype.constructor = Coroutine$doRunBlocking$lambda;
  Coroutine$doRunBlocking$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$closure$block(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function doRunBlocking$lambda(closure$block_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$doRunBlocking$lambda(closure$block_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function doRunBlocking(block) {
    return promise(coroutines.GlobalScope, void 0, void 0, doRunBlocking$lambda(block));
  }
  function getResource(name) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', name, false);
    xhr.send();
    if (equals(xhr.status, 200)) {
      return xhr.responseText;
    }
    throw Exception_init('failed to load resource ' + name + ': ' + xhr.status + ' ' + xhr.responseText);
  }
  function getDisplay() {
    return new JsDisplay();
  }
  function JsDisplay() {
  }
  JsDisplay.prototype.forNetwork = function () {
    return new JsNetworkDisplay(document);
  };
  JsDisplay.prototype.forPinky = function () {
    return new JsPinkyDisplay(ensureNotNull(document.getElementById('pinkyView')));
  };
  JsDisplay.prototype.forBrain = function () {
    return new JsBrainDisplay(ensureNotNull(document.getElementById('brainsView')));
  };
  JsDisplay.prototype.forMapper = function () {
    return new JsMapperDisplay(ensureNotNull(document.getElementById('mapperView')));
  };
  JsDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsDisplay',
    interfaces: [Display]
  };
  function JsNetworkDisplay(document) {
    this.packetsReceivedSpan_0 = ensureNotNull(document.getElementById('networkPacketsReceived'));
    this.packetsDroppedSpan_0 = ensureNotNull(document.getElementById('networkPacketsDropped'));
    this.packetsReceived_0 = 0;
    this.packetsDropped_0 = 0;
  }
  JsNetworkDisplay.prototype.receivedPacket = function () {
    var tmp$;
    clear(this.packetsReceivedSpan_0);
    appendText(this.packetsReceivedSpan_0, (tmp$ = this.packetsReceived_0, this.packetsReceived_0 = tmp$ + 1 | 0, tmp$).toString());
  };
  JsNetworkDisplay.prototype.droppedPacket = function () {
    var tmp$;
    clear(this.packetsDroppedSpan_0);
    appendText(this.packetsDroppedSpan_0, (tmp$ = this.packetsDropped_0, this.packetsDropped_0 = tmp$ + 1 | 0, tmp$).toString());
  };
  JsNetworkDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsNetworkDisplay',
    interfaces: [NetworkDisplay]
  };
  function JsPinkyDisplay(element) {
    this.consoleDiv_0 = null;
    this.beat1_0 = null;
    this.beat2_0 = null;
    this.beat3_0 = null;
    this.beat4_0 = null;
    this.beats_0 = null;
    this.brainCountDiv_0 = null;
    appendText(element, 'Brains online: ');
    this.brainCountDiv_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda);
    var beatsDiv = appendElement(element, 'div', JsPinkyDisplay_init$lambda_0);
    this.beat1_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_1);
    this.beat2_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_2);
    this.beat3_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_3);
    this.beat4_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_4);
    this.beats_0 = listOf([this.beat1_0, this.beat2_0, this.beat3_0, this.beat4_0]);
    this.consoleDiv_0 = appendElement(element, 'div', JsPinkyDisplay_init$lambda_5);
    this.brainCount_tt9c5b$_0 = 0;
    this.beat_o13evy$_0 = 0;
  }
  Object.defineProperty(JsPinkyDisplay.prototype, 'brainCount', {
    get: function () {
      return this.brainCount_tt9c5b$_0;
    },
    set: function (value) {
      clear(this.brainCountDiv_0);
      appendText(this.brainCountDiv_0, value.toString());
      this.brainCount_tt9c5b$_0 = value;
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'beat', {
    get: function () {
      return this.beat_o13evy$_0;
    },
    set: function (value) {
      clear_0(this.beats_0.get_za3lpa$(this.beat_o13evy$_0).classList);
      this.beats_0.get_za3lpa$(value).classList.add('selected');
      this.beat_o13evy$_0 = value;
    }
  });
  function JsPinkyDisplay_init$lambda($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda$lambda($receiver) {
    appendText($receiver, 'Beats');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_0($receiver) {
    $receiver.id = 'beatsDiv';
    appendElement($receiver, 'b', JsPinkyDisplay_init$lambda$lambda);
    appendText($receiver, ' ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_1($receiver) {
    appendText($receiver, '1');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_2($receiver) {
    appendText($receiver, '2');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_3($receiver) {
    appendText($receiver, '3');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_4($receiver) {
    appendText($receiver, '4');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_5($receiver) {
    return Unit;
  }
  JsPinkyDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsPinkyDisplay',
    interfaces: [PinkyDisplay]
  };
  function JsBrainDisplay(element) {
    this.myDiv_0 = appendElement(element, 'div', JsBrainDisplay$myDiv$lambda);
  }
  JsBrainDisplay.prototype.haveLink_6qu7we$ = function (link) {
    this.clearClasses_0();
    this.myDiv_0.classList.add('brain-link');
  };
  JsBrainDisplay.prototype.clearClasses_0 = function () {
    clear_0(this.myDiv_0.classList);
  };
  function JsBrainDisplay$myDiv$lambda($receiver) {
    addClass($receiver, ['brain-offline']);
    return Unit;
  }
  JsBrainDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsBrainDisplay',
    interfaces: [BrainDisplay]
  };
  function JsMapperDisplay(element) {
    this.element = element;
    this.startButton_0 = ensureNotNull(ensureNotNull(this.element.ownerDocument).getElementById('mapperStartButton'));
    this.stopButton_0 = ensureNotNull(ensureNotNull(this.element.ownerDocument).getElementById('mapperStopButton'));
    this.onStart_4s3d1r$_0 = null;
    this.onStop_dwgv5t$_0 = null;
    this.updateButtons_0(false);
    this.startButton_0.addEventListener('click', JsMapperDisplay_init$lambda(this));
    this.stopButton_0.addEventListener('click', JsMapperDisplay_init$lambda_0(this));
  }
  Object.defineProperty(JsMapperDisplay.prototype, 'onStart', {
    get: function () {
      return this.onStart_4s3d1r$_0;
    },
    set: function (onStart) {
      this.onStart_4s3d1r$_0 = onStart;
    }
  });
  Object.defineProperty(JsMapperDisplay.prototype, 'onStop', {
    get: function () {
      return this.onStop_dwgv5t$_0;
    },
    set: function (onStop) {
      this.onStop_dwgv5t$_0 = onStop;
    }
  });
  JsMapperDisplay.prototype.updateButtons_0 = function (isRunning) {
    set_disabled(this.startButton_0, isRunning);
    set_disabled(this.stopButton_0, !isRunning);
  };
  function JsMapperDisplay_init$lambda(this$JsMapperDisplay) {
    return function (it) {
      var tmp$;
      this$JsMapperDisplay.updateButtons_0(true);
      (tmp$ = this$JsMapperDisplay.onStart) != null ? tmp$() : null;
      return Unit;
    };
  }
  function JsMapperDisplay_init$lambda_0(this$JsMapperDisplay) {
    return function (it) {
      var tmp$;
      this$JsMapperDisplay.updateButtons_0(false);
      (tmp$ = this$JsMapperDisplay.onStop) != null ? tmp$() : null;
      return Unit;
    };
  }
  JsMapperDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsMapperDisplay',
    interfaces: [MapperDisplay]
  };
  function get_disabled($receiver) {
    return equals($receiver.getAttribute('disabled'), 'disabled');
  }
  function set_disabled($receiver, value) {
    if (value) {
      $receiver.setAttribute('disabled', 'disabled');
    }
     else {
      $receiver.removeAttribute('disabled');
    }
  }
  function clear_0($receiver) {
    while ($receiver.length > 0) {
      $receiver.remove(ensureNotNull($receiver.item(0)));
    }
  }
  function forEach($receiver, action) {
    var tmp$;
    tmp$ = $receiver.length;
    for (var i = 0; i < tmp$; i++) {
      action(ensureNotNull($receiver.item(i)));
    }
  }
  function getTimeMillis() {
    return Kotlin.Long.fromNumber((new Date()).getTime());
  }
  function Sample() {
  }
  Sample.prototype.checkMe = function () {
    return 12;
  };
  Sample.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Sample',
    interfaces: []
  };
  function Platform() {
    Platform_instance = this;
    this.name = 'JS';
  }
  Platform.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Platform',
    interfaces: []
  };
  var Platform_instance = null;
  function Platform_getInstance() {
    if (Platform_instance === null) {
      new Platform();
    }
    return Platform_instance;
  }
  var package$baaahs = _.baaahs || (_.baaahs = {});
  package$baaahs.Brain = Brain;
  package$baaahs.SimBrain = SimBrain;
  package$baaahs.Display = Display;
  package$baaahs.NetworkDisplay = NetworkDisplay;
  package$baaahs.PinkyDisplay = PinkyDisplay;
  package$baaahs.BrainDisplay = BrainDisplay;
  package$baaahs.MapperDisplay = MapperDisplay;
  _.ThingWithMass = ThingWithMass;
  _.Animal = Animal;
  _.Cat = Cat;
  _.Dog = Dog;
  _.Glass = Glass;
  _.kevmoMain = kevmoMain;
  Object.defineProperty(package$baaahs, 'main', {
    get: get_main,
    set: set_main
  });
  package$baaahs.Main = Main;
  package$baaahs.JsPanel = JsPanel;
  package$baaahs.Mapper = Mapper;
  Network.Link = Network$Link;
  Network.Address = Network$Address;
  Network.Listener = Network$Listener;
  package$baaahs.Network = Network;
  package$baaahs.FakeNetwork = FakeNetwork;
  Pinky.BeatProvider = Pinky$BeatProvider;
  package$baaahs.Pinky = Pinky;
  package$baaahs.RemoteBrain = RemoteBrain;
  package$baaahs.SomeDumbShow = SomeDumbShow;
  Object.defineProperty(Ports, 'Companion', {
    get: Ports$Companion_getInstance
  });
  package$baaahs.Ports = Ports;
  Object.defineProperty(Type, 'BRAIN_HELLO', {
    get: Type$BRAIN_HELLO_getInstance
  });
  Object.defineProperty(Type, 'BRAIN_PANEL_SHADE', {
    get: Type$BRAIN_PANEL_SHADE_getInstance
  });
  Object.defineProperty(Type, 'MAPPER_HELLO', {
    get: Type$MAPPER_HELLO_getInstance
  });
  Object.defineProperty(Type, 'PINKY_PONG', {
    get: Type$PINKY_PONG_getInstance
  });
  package$baaahs.Type = Type;
  package$baaahs.parse_fqrh44$ = parse;
  package$baaahs.BrainHelloMessage = BrainHelloMessage;
  Object.defineProperty(BrainShaderMessage, 'Companion', {
    get: BrainShaderMessage$Companion_getInstance
  });
  package$baaahs.BrainShaderMessage = BrainShaderMessage;
  Object.defineProperty(MapperHelloMessage, 'Companion', {
    get: MapperHelloMessage$Companion_getInstance
  });
  package$baaahs.MapperHelloMessage = MapperHelloMessage;
  Object.defineProperty(PinkyPongMessage, 'Companion', {
    get: PinkyPongMessage$Companion_getInstance
  });
  package$baaahs.PinkyPongMessage = PinkyPongMessage;
  package$baaahs.Message = Message;
  SheepModel.Point = SheepModel$Point;
  SheepModel.Line = SheepModel$Line;
  SheepModel.Face = SheepModel$Face;
  SheepModel.Faces = SheepModel$Faces;
  SheepModel.Panel = SheepModel$Panel;
  package$baaahs.SheepModel = SheepModel;
  Object.defineProperty(Color, 'Companion', {
    get: Color$Companion_getInstance
  });
  package$baaahs.Color = Color;
  package$baaahs.ByteArrayWriter_init_za3lpa$ = ByteArrayWriter_init;
  package$baaahs.ByteArrayWriter = ByteArrayWriter;
  package$baaahs.ByteArrayReader = ByteArrayReader;
  var package$sample = _.sample || (_.sample = {});
  package$sample.hello = hello;
  package$baaahs.doRunBlocking_g2bo5h$ = doRunBlocking;
  package$baaahs.getResource_61zpoe$ = getResource;
  package$baaahs.getDisplay = getDisplay;
  package$baaahs.JsDisplay = JsDisplay;
  package$baaahs.JsNetworkDisplay = JsNetworkDisplay;
  package$baaahs.JsPinkyDisplay = JsPinkyDisplay;
  package$baaahs.JsBrainDisplay = JsBrainDisplay;
  package$baaahs.JsMapperDisplay = JsMapperDisplay;
  package$baaahs.forEach_dokpt5$ = forEach;
  package$baaahs.getTimeMillis = getTimeMillis;
  package$sample.Sample = Sample;
  Object.defineProperty(package$sample, 'Platform', {
    get: Platform_getInstance
  });
  FakeNetwork$FakeLink.prototype.send_bkw8fl$ = Network$Link.prototype.send_bkw8fl$;
  FakeNetwork$FakeLink.prototype.broadcast_ecsl0t$ = Network$Link.prototype.broadcast_ecsl0t$;
  Kotlin.defineModule('play', _);
  return _;
}(typeof play === 'undefined' ? {} : play, kotlin, this['kotlinx-coroutines-core']);
