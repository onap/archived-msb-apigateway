/*
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
////合并hk.min.js
/*! store2 - v2.3.0 - 2015-05-22
* Copyright (c) 2015 Nathan Bubna; Licensed MIT, GPL */
;(function(window, define) {
    var _ = {
        version: "2.3.0",
        areas: {},
        apis: {},

        // utilities
        inherit: function(api, o) {
            for (var p in api) {
                if (!o.hasOwnProperty(p)) {
                    o[p] = api[p];
                }
            }
            return o;
        },
        stringify: function(d) {
            return d === undefined || typeof d === "function" ? d+'' : JSON.stringify(d);
        },
        parse: function(s) {
            // if it doesn't parse, return as is
            try{ return JSON.parse(s); }catch(e){ return s; }
        },

        // extension hooks
        fn: function(name, fn) {
            _.storeAPI[name] = fn;
            for (var api in _.apis) {
                _.apis[api][name] = fn;
            }
        },
        get: function(area, key){ return area.getItem(key); },
        set: function(area, key, string){ area.setItem(key, string); },
        remove: function(area, key){ area.removeItem(key); },
        key: function(area, i){ return area.key(i); },
        length: function(area){ return area.length; },
        clear: function(area){ area.clear(); },

        // core functions
        Store: function(id, area, namespace) {
            var store = _.inherit(_.storeAPI, function(key, data, overwrite) {
                if (arguments.length === 0){ return store.getAll(); }
                if (data !== undefined){ return store.set(key, data, overwrite); }
                if (typeof key === "string"){ return store.get(key); }
                if (!key){ return store.clear(); }
                return store.setAll(key, data);// overwrite=data, data=key
            });
            store._id = id;
            try {
                var testKey = '_safariPrivate_';
                area.setItem(testKey, 'sucks');
                store._area = area;
                area.removeItem(testKey);
            } catch (e) {}
            if (!store._area) {
                store._area = _.inherit(_.storageAPI, { items: {}, name: 'fake' });
            }
            store._ns = namespace || '';
            if (!_.areas[id]) {
                _.areas[id] = store._area;
            }
            if (!_.apis[store._ns+store._id]) {
                _.apis[store._ns+store._id] = store;
            }
            return store;
        },
        storeAPI: {
            // admin functions
            area: function(id, area) {
                var store = this[id];
                if (!store || !store.area) {
                    store = _.Store(id, area, this._ns);//new area-specific api in this namespace
                    if (!this[id]){ this[id] = store; }
                }
                return store;
            },
            namespace: function(namespace, noSession) {
                if (!namespace){
                    return this._ns ? this._ns.substring(0,this._ns.length-1) : '';
                }
                var ns = namespace, store = this[ns];
                if (!store || !store.namespace) {
                    store = _.Store(this._id, this._area, this._ns+ns+'.');//new namespaced api
                    if (!this[ns]){ this[ns] = store; }
                    if (!noSession){ store.area('session', _.areas.session); }
                }
                return store;
            },
            isFake: function(){ return this._area.name === 'fake'; },
            toString: function() {
                return 'store'+(this._ns?'.'+this.namespace():'')+'['+this._id+']';
            },

            // storage functions
            has: function(key) {
                if (this._area.has) {
                    return this._area.has(this._in(key));//extension hook
                }
                return !!(this._in(key) in this._area);
            },
            size: function(){ return this.keys().length; },
            each: function(fn, and) {
                for (var i=0, m=_.length(this._area); i<m; i++) {
                    var key = this._out(_.key(this._area, i));
                    if (key !== undefined) {
                        if (fn.call(this, key, and || this.get(key)) === false) {
                            break;
                        }
                    }
                    if (m > _.length(this._area)) { m--; i--; }// in case of removeItem
                }
                return and || this;
            },
            keys: function() {
                return this.each(function(k, list){ list.push(k); }, []);
            },
            get: function(key, alt) {
                var s = _.get(this._area, this._in(key));
                return s !== null ? _.parse(s) : alt || s;// support alt for easy default mgmt
            },
            getAll: function() {
                return this.each(function(k, all){ all[k] = this.get(k); }, {});
            },
            set: function(key, data, overwrite) {
                var d = this.get(key);
                if (d != null && overwrite === false) {
                    return data;
                }
                return _.set(this._area, this._in(key), _.stringify(data), overwrite) || d;
            },
            setAll: function(data, overwrite) {
                var changed, val;
                for (var key in data) {
                    val = data[key];
                    if (this.set(key, val, overwrite) !== val) {
                        changed = true;
                    }
                }
                return changed;
            },
            remove: function(key) {
                var d = this.get(key);
                _.remove(this._area, this._in(key));
                return d;
            },
            clear: function() {
                if (!this._ns) {
                    _.clear(this._area);
                } else {
                    this.each(function(k){ _.remove(this._area, this._in(k)); }, 1);
                }
                return this;
            },
            clearAll: function() {
                var area = this._area;
                for (var id in _.areas) {
                    if (_.areas.hasOwnProperty(id)) {
                        this._area = _.areas[id];
                        this.clear();
                    }
                }
                this._area = area;
                return this;
            },

            // internal use functions
            _in: function(k) {
                if (typeof k !== "string"){ k = _.stringify(k); }
                return this._ns ? this._ns + k : k;
            },
            _out: function(k) {
                return this._ns ?
                    k && k.indexOf(this._ns) === 0 ?
                        k.substring(this._ns.length) :
                        undefined : // so each() knows to skip it
                    k;
            }
        },// end _.storeAPI
        storageAPI: {
            length: 0,
            has: function(k){ return this.items.hasOwnProperty(k); },
            key: function(i) {
                var c = 0;
                for (var k in this.items){
                    if (this.has(k) && i === c++) {
                        return k;
                    }
                }
            },
            setItem: function(k, v) {
                if (!this.has(k)) {
                    this.length++;
                }
                this.items[k] = v;
            },
            removeItem: function(k) {
                if (this.has(k)) {
                    delete this.items[k];
                    this.length--;
                }
            },
            getItem: function(k){ return this.has(k) ? this.items[k] : null; },
            clear: function(){ for (var k in this.list){ this.removeItem(k); } },
            toString: function(){ return this.length+' items in '+this.name+'Storage'; }
        }// end _.storageAPI
    };

    // setup the primary store fn
    if (window.store){ _.conflict = window.store; }
    var store =
        // safely set this up (throws error in IE10/32bit mode for local files)
        _.Store("local", (function(){try{ return localStorage; }catch(e){}})());
    store.local = store;// for completeness
    store._ = _;// for extenders and debuggers...
    // safely setup store.session (throws exception in FF for file:/// urls)
    store.area("session", (function(){try{ return sessionStorage; }catch(e){}})());

    //Expose store to the global object
    window.store = store;

    if (typeof define === 'function' && define.amd !== undefined) {
        define(function () {
            return store;
        });
    } else if (typeof module !== 'undefined' && module.exports) {
        module.exports = store;
    }

})(this, null);

// XHook - v1.3.3 - https://github.com/jpillora/xhook
// Jaime Pillora <dev@jpillora.com> - MIT Copyright 2015
(function(window,undefined) {
var AFTER, BEFORE, COMMON_EVENTS, EventEmitter, FIRE, FormData, NativeFormData, NativeXMLHttp, OFF, ON, READY_STATE, UPLOAD_EVENTS, XHookFormData, XHookHttpRequest, XMLHTTP, convertHeaders, depricatedProp, document, fakeEvent, mergeObjects, msie, proxyEvents, slice, xhook, _base,
  __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

document = window.document;

BEFORE = 'before';

AFTER = 'after';

READY_STATE = 'readyState';

ON = 'addEventListener';

OFF = 'removeEventListener';

FIRE = 'dispatchEvent';

XMLHTTP = 'XMLHttpRequest';

FormData = 'FormData';

UPLOAD_EVENTS = ['load', 'loadend', 'loadstart'];

COMMON_EVENTS = ['progress', 'abort', 'error', 'timeout'];

msie = parseInt((/msie (\d+)/.exec(navigator.userAgent.toLowerCase()) || [])[1]);

if (isNaN(msie)) {
  msie = parseInt((/trident\/.*; rv:(\d+)/.exec(navigator.userAgent.toLowerCase()) || [])[1]);
}

(_base = Array.prototype).indexOf || (_base.indexOf = function(item) {
  var i, x, _i, _len;
  for (i = _i = 0, _len = this.length; _i < _len; i = ++_i) {
    x = this[i];
    if (x === item) {
      return i;
    }
  }
  return -1;
});

slice = function(o, n) {
  return Array.prototype.slice.call(o, n);
};

depricatedProp = function(p) {
  return p === "returnValue" || p === "totalSize" || p === "position";
};

mergeObjects = function(src, dst) {
  var k, v;
  for (k in src) {
    v = src[k];
    if (depricatedProp(k)) {
      continue;
    }
    try {
      dst[k] = src[k];
    } catch (_error) {}
  }
  return dst;
};

proxyEvents = function(events, src, dst) {
  var event, p, _i, _len;
  p = function(event) {
    return function(e) {
      var clone, k, val;
      clone = {};
      for (k in e) {
        if (depricatedProp(k)) {
          continue;
        }
        val = e[k];
        clone[k] = val === src ? dst : val;
      }
      return dst[FIRE](event, clone);
    };
  };
  for (_i = 0, _len = events.length; _i < _len; _i++) {
    event = events[_i];
    if (dst._has(event)) {
      src["on" + event] = p(event);
    }
  }
};

fakeEvent = function(type) {
  var msieEventObject;
  if (document.createEventObject != null) {
    msieEventObject = document.createEventObject();
    msieEventObject.type = type;
    return msieEventObject;
  } else {
    try {
      return new Event(type);
    } catch (_error) {
      return {
        type: type
      };
    }
  }
};

EventEmitter = function(nodeStyle) {
  var emitter, events, listeners;
  events = {};
  listeners = function(event) {
    return events[event] || [];
  };
  emitter = {};
  emitter[ON] = function(event, callback, i) {
    events[event] = listeners(event);
    if (events[event].indexOf(callback) >= 0) {
      return;
    }
    i = i === undefined ? events[event].length : i;
    events[event].splice(i, 0, callback);
  };
  emitter[OFF] = function(event, callback) {
    var i;
    if (event === undefined) {
      events = {};
      return;
    }
    if (callback === undefined) {
      events[event] = [];
    }
    i = listeners(event).indexOf(callback);
    if (i === -1) {
      return;
    }
    listeners(event).splice(i, 1);
  };
  emitter[FIRE] = function() {
    var args, event, i, legacylistener, listener, _i, _len, _ref;
    args = slice(arguments);
    event = args.shift();
    if (!nodeStyle) {
      args[0] = mergeObjects(args[0], fakeEvent(event));
    }
    legacylistener = emitter["on" + event];
    if (legacylistener) {
      legacylistener.apply(undefined, args);
    }
    _ref = listeners(event).concat(listeners("*"));
    for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
      listener = _ref[i];
      listener.apply(undefined, args);
    }
  };
  emitter._has = function(event) {
    return !!(events[event] || emitter["on" + event]);
  };
  if (nodeStyle) {
    emitter.listeners = function(event) {
      return slice(listeners(event));
    };
    emitter.on = emitter[ON];
    emitter.off = emitter[OFF];
    emitter.fire = emitter[FIRE];
    emitter.once = function(e, fn) {
      var fire;
      fire = function() {
        emitter.off(e, fire);
        return fn.apply(null, arguments);
      };
      return emitter.on(e, fire);
    };
    emitter.destroy = function() {
      return events = {};
    };
  }
  return emitter;
};

xhook = EventEmitter(true);

xhook.EventEmitter = EventEmitter;

xhook[BEFORE] = function(handler, i) {
  if (handler.length < 1 || handler.length > 2) {
    throw "invalid hook";
  }
  return xhook[ON](BEFORE, handler, i);
};

xhook[AFTER] = function(handler, i) {
  if (handler.length < 2 || handler.length > 3) {
    throw "invalid hook";
  }
  return xhook[ON](AFTER, handler, i);
};

xhook.enable = function() {
  window[XMLHTTP] = XHookHttpRequest;
  if (NativeFormData) {
    window[FormData] = XHookFormData;
  }
};

xhook.disable = function() {
  window[XMLHTTP] = xhook[XMLHTTP];
  window[FormData] = NativeFormData;
};

convertHeaders = xhook.headers = function(h, dest) {
  var header, headers, k, name, v, value, _i, _len, _ref;
  if (dest == null) {
    dest = {};
  }
  switch (typeof h) {
    case "object":
      headers = [];
      for (k in h) {
        v = h[k];
        name = k.toLowerCase();
        headers.push("" + name + ":\t" + v);
      }
      return headers.join('\n');
    case "string":
      headers = h.split('\n');
      for (_i = 0, _len = headers.length; _i < _len; _i++) {
        header = headers[_i];
        if (/([^:]+):\s*(.+)/.test(header)) {
          name = (_ref = RegExp.$1) != null ? _ref.toLowerCase() : void 0;
          value = RegExp.$2;
          if (dest[name] == null) {
            dest[name] = value;
          }
        }
      }
      return dest;
  }
};

NativeFormData = window[FormData];

XHookFormData = function(form) {
  var entries;
  this.fd = form ? new NativeFormData(form) : new NativeFormData();
  this.form = form;
  entries = [];
  Object.defineProperty(this, 'entries', {
    get: function() {
      var fentries;
      fentries = !form ? [] : slice(form.querySelectorAll("input,select")).filter(function(e) {
        var _ref;
        return ((_ref = e.type) !== 'checkbox' && _ref !== 'radio') || e.checked;
      }).map(function(e) {
        return [e.name, e.type === "file" ? e.files : e.value];
      });
      return fentries.concat(entries);
    }
  });
  this.append = (function(_this) {
    return function() {
      var args;
      args = slice(arguments);
      entries.push(args);
      return _this.fd.append.apply(_this.fd, args);
    };
  })(this);
};

if (NativeFormData) {
  xhook[FormData] = NativeFormData;
  window[FormData] = XHookFormData;
}

NativeXMLHttp = window[XMLHTTP];

xhook[XMLHTTP] = NativeXMLHttp;

XHookHttpRequest = window[XMLHTTP] = function() {
  var ABORTED, currentState, emitFinal, emitReadyState, facade, hasError, hasErrorHandler, readBody, readHead, request, response, setReadyState, status, transiting, writeBody, writeHead, xhr;
  ABORTED = -1;
  xhr = new xhook[XMLHTTP]();
  request = {};
  status = null;
  hasError = void 0;
  transiting = void 0;
  response = void 0;
  readHead = function() {
    var key, name, val, _ref;
    response.status = status || xhr.status;
    if (!(status === ABORTED && msie < 10)) {
      response.statusText = xhr.statusText;
    }
    if (status !== ABORTED) {
      _ref = convertHeaders(xhr.getAllResponseHeaders());
      for (key in _ref) {
        val = _ref[key];
        if (!response.headers[key]) {
          name = key.toLowerCase();
          response.headers[name] = val;
        }
      }
    }
  };
  readBody = function() {
    if (!xhr.responseType || xhr.responseType === "text") {
      response.text = xhr.responseText;
      response.data = xhr.responseText;
    } else if (xhr.responseType === "document") {
      response.xml = xhr.responseXML;
      response.data = xhr.responseXML;
    } else {
      response.data = xhr.response;
    }
	try{
		if(response.data){
			// checked login time out reload to login
			var data = $.parseJSON(response.data);
			if(data.source && data.redirect_uri && data.source === "smagent"){
				var redirect_uri = data.redirect_uri;
				if(top){
					top.location = redirect_uri.replace('$1', encodeURIComponent(top.location));
				}else{
					window.location = redirect_uri.replace('$1', encodeURIComponent(window.location));
				}
			}
		}
	}catch(e){}
	
    if ("responseURL" in xhr) {
      response.finalUrl = xhr.responseURL;
    }
  };
  writeHead = function() {
    facade.status = response.status;
    facade.statusText = response.statusText;
  };
  writeBody = function() {
    if ('text' in response) {
      facade.responseText = response.text;
    }
    if ('xml' in response) {
      facade.responseXML = response.xml;
    }
    if ('data' in response) {
      facade.response = response.data;
    }
    if ('finalUrl' in response) {
      facade.responseURL = response.finalUrl;
    }
  };
  emitReadyState = function(n) {
    while (n > currentState && currentState < 4) {
      facade[READY_STATE] = ++currentState;
      if (currentState === 1) {
        facade[FIRE]("loadstart", {});
      }
      if (currentState === 2) {
        writeHead();
      }
      if (currentState === 4) {
        writeHead();
        writeBody();
      }
      facade[FIRE]("readystatechange", {});
      if (currentState === 4) {
        setTimeout(emitFinal, 0);
      }
    }
  };
  emitFinal = function() {
    if (!hasError) {
      facade[FIRE]("load", {});
    }
    facade[FIRE]("loadend", {});
    if (hasError) {
      facade[READY_STATE] = 0;
    }
  };
  currentState = 0;
  setReadyState = function(n) {
    var hooks, process;
    if (n !== 4) {
      emitReadyState(n);
      return;
    }
    hooks = xhook.listeners(AFTER);
    process = function() {
      var hook;
      if (!hooks.length) {
        return emitReadyState(4);
      }
      hook = hooks.shift();
      if (hook.length === 2) {
        hook(request, response);
        return process();
      } else if (hook.length === 3 && request.async) {
        return hook(request, response, process);
      } else {
        return process();
      }
    };
    process();
  };
  facade = request.xhr = EventEmitter();
  xhr.onreadystatechange = function(event) {
    try {
      if (xhr[READY_STATE] === 2) {
        readHead();
      }
    } catch (_error) {}
    if (xhr[READY_STATE] === 4) {
      transiting = false;
      readHead();
      readBody();
    }
    setReadyState(xhr[READY_STATE]);
  };
  hasErrorHandler = function() {
    hasError = true;
  };
  facade[ON]('error', hasErrorHandler);
  facade[ON]('timeout', hasErrorHandler);
  facade[ON]('abort', hasErrorHandler);
  facade[ON]('progress', function() {
    if (currentState < 3) {
      setReadyState(3);
    } else {
      facade[FIRE]("readystatechange", {});
    }
  });
  if ('withCredentials' in xhr || xhook.addWithCredentials) {
    facade.withCredentials = false;	
  }
  facade.status = 0;
  facade.open = function(method, url, async, user, pass) {
    currentState = 0;
    hasError = false;
    transiting = false;
    request.headers = {};
    request.headerNames = {};
    request.status = 0;
    response = {};
    response.headers = {};
    request.method = method;
    request.url = url;
    request.async = async !== false;
    request.user = user;
    request.pass = pass;
    setReadyState(1);
  };
  facade.send = function(body) {
    var hooks, k, modk, process, send, _i, _len, _ref;
    _ref = ['type', 'timeout', 'withCredentials'];
	if(navigator.userAgent.indexOf("Firefox/") != -1){http://atmosphere-framework.2306103.n4.nabble.com/Atmosphere-js-withCredentials-true-does-not-work-in-Firefox-td4656661.html
		_ref = ['type', 'timeout'];	  
	}
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      k = _ref[_i];
      modk = k === "type" ? "responseType" : k;
      if (modk in facade) {
        request[k] = facade[modk];
      }
    }
    request.body = body;
    send = function() {
      var header, value, _j, _len1, _ref1, _ref2;
      proxyEvents(COMMON_EVENTS, xhr, facade);
      if (facade.upload) {
        proxyEvents(COMMON_EVENTS.concat(UPLOAD_EVENTS), xhr.upload, facade.upload);
      }
      transiting = true;
      xhr.open(request.method, request.url, request.async, request.user, request.pass);
      _ref1 = ['type', 'timeout', 'withCredentials'];
	  if(navigator.userAgent.indexOf("Firefox/") != -1){//http://atmosphere-framework.2306103.n4.nabble.com/Atmosphere-js-withCredentials-true-does-not-work-in-Firefox-td4656661.html
		_ref1 = ['type', 'timeout'];	  
	  }
      for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
        k = _ref1[_j];
        modk = k === "type" ? "responseType" : k;
        if (k in request) {
          xhr[modk] = request[k];
        }
      }
      _ref2 = request.headers;
      for (header in _ref2) {
        value = _ref2[header];
        xhr.setRequestHeader(header, value);
      }
      if (request.body instanceof XHookFormData) {
        request.body = request.body.fd;
      }
      xhr.send(request.body);
    };
    hooks = xhook.listeners(BEFORE);
    process = function() {
      var done, hook;
      if (!hooks.length) {
        return send();
      }
      done = function(userResponse) {
        if (typeof userResponse === 'object' && (typeof userResponse.status === 'number' || typeof response.status === 'number')) {
          mergeObjects(userResponse, response);
          if (__indexOf.call(userResponse, 'data') < 0) {
            userResponse.data = userResponse.response || userResponse.text;
          }
          setReadyState(4);
          return;
        }
        process();
      };
      done.head = function(userResponse) {
        mergeObjects(userResponse, response);
        return setReadyState(2);
      };
      done.progress = function(userResponse) {
        mergeObjects(userResponse, response);
        return setReadyState(3);
      };
      hook = hooks.shift();
      if (hook.length === 1) {
        return done(hook(request));
      } else if (hook.length === 2 && request.async) {
        return hook(request, done);
      } else {
        return done();
      }
    };
    process();
  };
  facade.abort = function() {
    status = ABORTED;
    if (transiting) {
      xhr.abort();
    } else {
      facade[FIRE]('abort', {});
    }
  };
  facade.setRequestHeader = function(header, value) {
    var lName, name;
    lName = header != null ? header.toLowerCase() : void 0;
    name = request.headerNames[lName] = request.headerNames[lName] || header;
    if (request.headers[name]) {
      value = request.headers[name] + ', ' + value;
    }
    request.headers[name] = value;
  };
  facade.getResponseHeader = function(header) {
    var name;
    name = header != null ? header.toLowerCase() : void 0;
    return response.headers[name];
  };
  facade.getAllResponseHeaders = function() {
    return convertHeaders(response.headers);
  };
  if (xhr.overrideMimeType) {
    facade.overrideMimeType = function() {
      return xhr.overrideMimeType.apply(xhr, arguments);
    };
  }
  if (xhr.upload) {
    facade.upload = request.upload = EventEmitter();
  }
  return facade;
};
/*
if (typeof this.define === "function" && this.define.amd) {
  define("xhook", [], function() {
    return xhook;
  });
} else {*/
  (this.exports || this).xhook = xhook;
//}

}.call(this,window));

xhook.before(function(request) {
	var zte_headers = store('zte_http_headers');
	if (zte_headers && zte_headers.length > 0) {
		for (i = 0; i < zte_headers.length; i++) {
			if (zte_headers[i].store === true) {
				if ( !! store(zte_headers[i].value)) {
					request.headers[zte_headers[i].key] = store(zte_headers[i].value);
				}
			} else {
				request.headers[zte_headers[i].key] = zte_headers[i].value;
			}
		}
	}
});
/**
 * 初始化脚本文件装载工具
 * zongying 2010.12
 *  modify：
 */ 
$Boot = {};

/**
 * 创建命名空间
 * @param {Object} name
 * @param {Object} object
 */
$Boot.createNamespace = function(name, object) {
    var splits = name.split(".");
    var parent = window;
    //document.window浏览器内置对象
    var part = splits[0];
    for (var i = 0, len = splits.length - 1; i < len; i++, part = splits[i]) {
        if (!parent[part]) {
            parent = parent[part] = {};
        } else {
            parent = parent[part];
        }
    }
    // 存放对象
    parent[part] = object;
    // 返回 last part name (例如：classname)
    return part;
}

$Boot.isDefined = function(o) {
    return typeof (o) != "undefined"
}
/**
 * 启动配置类
 */
$Boot.Config = function() {

    function isDefined(o) {
        return typeof (o) != "undefined"
    }

    //用户应用当前目录
    if (!isDefined(window.$userAppDir)) {
        window.$userAppDir = './'
    }
    //组件库目录
    if (!isDefined(window.$userFrameDir)) {
        window.$userFrameDir = '/common/'
    }
    //用户i18文件目录
    if (!isDefined(window.$userI18nDir)) {
        window.$userI18nDir = './'
    }

    //当前语言 默认为英语
    var language = "zh-CN";
    //var languageList = ['ar', 'ba', 'cr', 'cs', 'de', 'el', 'es', 'fi', 'fr', 'fr-FR', 'hu-HU', 'id', 'it', 'ja', 'nb-NO', 'nl', 'pl', 'pl-PL', 'pt', 'pt-BR', 'ro-RO', 'ru-RU', 'sk', 'sr', 'sr-Latn', 'sv-SE', 'en-US','uk-UA', 'zh-CN', 'zh-TW'];
    var languageList = ['en-US', 'zh-CN'];
	
	//从服务端取客户端接受语言类型
    var getAcceptLangFromServer = true;
	
	
	    /**
     * 创建XMLHttpRequest对象
     */
    function createXMLHttpRequest() {
        if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } else if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else {
            throw new Error("This Brower do not support XMLHTTP!!");
        }
    }

	
	    /**
     * 同步发送xml http  请求
     * @param {Object} url
     * @param {Object} data
     * @param {Object} method
     */
    function httpRequest(method, url, data) {
        var xmlhttp;
        xmlhttp = createXMLHttpRequest();
        var sendData = null;
        if (method == "get") {
            url = url + "?" + data;

        } else if (method == "post") {
            sendData = data;
        }
        xmlhttp.open(method, url, false);
        xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        xmlhttp.setRequestHeader("If-Modified-Since", "0");
        xmlhttp.send(sendData);
        if (xmlhttp.status == 200)
            return xmlhttp.responseText;
    }
	
	    /**
     * 同步发送xml http  请求（给外部调用）
     * @param {Object} url
     * @param {Object} data
     * @param {Object} method
     */
    this.httpRequestStatic = function(method, url, data) {
        var xmlhttp;
        xmlhttp = createXMLHttpRequest();
        var sendData = null;
        if (method == "get") {
            url = url + "?" + data;

        } else if (method == "post") {
            sendData = data;
        }
        xmlhttp.open(method, url, false);
        xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        xmlhttp.setRequestHeader("If-Modified-Since", "0");
        xmlhttp.send(sendData);
        if (xmlhttp.status == 200)
            return xmlhttp.responseText;
    }
	
	function inArray(array, obj) {
        for (var i = 0; i < array.length; i++) {
            if (array[i] == obj) {
                return true;
            }
        }
        return false;

    }
	
    /**
     * 取得浏览器语言信息
     */
    this.getLanguage = function() {        
		var rtnLanguage = localStorage.getItem("language-option");		
		if( rtnLanguage == "null" || rtnLanguage == null ){
			rtnLanguage = window.navigator.userLanguage||window.navigator.language;
		}
		if( rtnLanguage == '"zh-CN"' || rtnLanguage == "zh-CN" ){
			return "zh-CN";
		}else{
			return "en-US";
		}
		//return "en-US";
    }

	this.getUrlParam=function(name){
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var search =decodeURIComponent(location.search.substring(1)); //decodeURIComponent() 函数可对 encodeURIComponent() 函数编码的 URI 进行解码。
            var r =search.match(reg);  //匹配目标参数
            if (r != null) return unescape(r[2]); //unescape() 函数可对通过 escape() 编码的字符串进行解码。
            return null; //返回参数值
    }


}

//创建命名空间
$Boot.createNamespace("com.zte.ums.aos.framework.BootConfig", $Boot.Config);
//创建基础配置对象实例
$Boot.bootConfig = new com.zte.ums.aos.framework.BootConfig();

function getLanguage(){
   return $Boot.bootConfig.getLanguage();
}

function getStringWidth(text,fontSize)
{
    var span = document.getElementById("_ictframework_getwidth");
    if (span == null) {
        span = document.createElement("span");
        span.id = "_ictframework_getwidth";
        document.body.appendChild(span);
    }
    span.innerText = text;
    span.style.whiteSpace = "nowrap";
    $("#_ictframework_getwidth").attr('style','font-size:'+fontSize+'px;');
	var width = span.offsetWidth;
	$("#_ictframework_getwidth").attr('style','display:none');
    return width;
}

function getUrlParam(name){
    return $Boot.bootConfig.getUrlParam(name);
}

function httpRequest(method, url, data) {
    return $Boot.bootConfig.httpRequestStatic(method, url, data)
}

// 定义JQUERY AJAX 完成函数，判断返回状态，如果状态正常，但HEADER头里有session超时信息，则刷新重登录
// 如果状态为 401, 也刷新重登录
// 注意如果在$.ajax() 函数中定义了 complete，则覆盖了这里预定义complete内容，以$.ajax()函数中定义的为准，这里预定义的函数则失效，如果
// 要继续判断session超时，则需要在 $.ajax()函数中定义的complete函数中加入这里预定义内容。
if (jQuery) {
	$.ajaxSetup({
		complete:function(XMLHttpRequest,textStatus){
			 if (XMLHttpRequest.status == 401) {
				window.location.replace("login.html"); 
			 }
			 // if (XMLHttpRequest.status == 200) {
				// var sessionstatus=XMLHttpRequest.getResponseHeader("sessionstatus"); ////通过XMLHttpRequest取得响应头，sessionstatus，  
				 // if(sessionstatus=="timeout"){
					 // window.location.replace("/");
				 // }
			 // } else if (XMLHttpRequest.status == 401) {
				// window.location.replace("/");
			 // } 
		}
	});
}