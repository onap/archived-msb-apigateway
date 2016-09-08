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
 * Locale: DE (German, Deutsch)
 */
$.extend($.validator.messages, {
	required: "Dieses Feld ist ein Pflichtfeld.",
	maxlength: $.validator.format("Geben Sie bitte maximal {0} Zeichen ein."),
	minlength: $.validator.format("Geben Sie bitte mindestens {0} Zeichen ein."),
	rangelength: $.validator.format("Geben Sie bitte mindestens {0} und maximal {1} Zeichen ein."),
	email: "Geben Sie bitte eine gültige E-Mail Adresse ein.",
	url: "Geben Sie bitte eine gültige URL ein.",
	date: "Bitte geben Sie ein gültiges Datum ein.",
	number: "Geben Sie bitte eine Nummer ein.",
	digits: "Geben Sie bitte nur Ziffern ein.",
	equalTo: "Bitte denselben Wert wiederholen.",
	range: $.validator.format("Geben Sie bitte einen Wert zwischen {0} und {1} ein."),
	max: $.validator.format("Geben Sie bitte einen Wert kleiner oder gleich {0} ein."),
	min: $.validator.format("Geben Sie bitte einen Wert größer oder gleich {0} ein."),
	creditcard: "Geben Sie bitte eine gültige Kreditkarten-Nummer ein."
});

}));