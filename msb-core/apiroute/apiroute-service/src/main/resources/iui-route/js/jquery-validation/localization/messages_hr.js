/*
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *     Author: Zhaoxing Meng
 *     email: meng.zhaoxing1@zte.com.cn
 */
(function( factory ) {
	if ( typeof define === "function" && define.amd ) {
		define( ["jquery", "../jquery.validate"], factory );
	} else {
		factory( jQuery );
	}
}(function( $ ) {

/*
 * Translated default messages for the jQuery validation plugin.
 * Locale: HR (Croatia; hrvatski jezik)
 */
$.extend($.validator.messages, {
	required: "Ovo polje je obavezno.",
	remote: "Ovo polje treba popraviti.",
	email: "Unesite ispravnu e-mail adresu.",
	url: "Unesite ispravan URL.",
	date: "Unesite ispravan datum.",
	dateISO: "Unesite ispravan datum (ISO).",
	number: "Unesite ispravan broj.",
	digits: "Unesite samo brojeve.",
	creditcard: "Unesite ispravan broj kreditne kartice.",
	equalTo: "Unesite ponovo istu vrijednost.",
	extension: "Unesite vrijednost sa ispravnom ekstenzijom.",
	maxlength: $.validator.format("Maksimalni broj znakova je {0} ."),
	minlength: $.validator.format("Minimalni broj znakova je {0} ."),
	rangelength: $.validator.format("Unesite vrijednost između {0} i {1} znakova."),
	range: $.validator.format("Unesite vrijednost između {0} i {1}."),
	max: $.validator.format("Unesite vrijednost manju ili jednaku {0}."),
	min: $.validator.format("Unesite vrijednost veću ili jednaku {0}.")
});

}));