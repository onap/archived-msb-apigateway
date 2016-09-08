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
 * Locale: IT (Italian; Italiano)
 */
$.extend($.validator.messages, {
	required: "Campo obbligatorio.",
	remote: "Controlla questo campo.",
	email: "Inserisci un indirizzo email valido.",
	url: "Inserisci un indirizzo web valido.",
	date: "Inserisci una data valida.",
	dateISO: "Inserisci una data valida (ISO).",
	number: "Inserisci un numero valido.",
	digits: "Inserisci solo numeri.",
	creditcard: "Inserisci un numero di carta di credito valido.",
	equalTo: "Il valore non corrisponde.",
	extension: "Inserisci un valore con un&apos;estensione valida.",
	maxlength: $.validator.format("Non inserire pi&ugrave; di {0} caratteri."),
	minlength: $.validator.format("Inserisci almeno {0} caratteri."),
	rangelength: $.validator.format("Inserisci un valore compreso tra {0} e {1} caratteri."),
	range: $.validator.format("Inserisci un valore compreso tra {0} e {1}."),
	max: $.validator.format("Inserisci un valore minore o uguale a {0}."),
	min: $.validator.format("Inserisci un valore maggiore o uguale a {0}."),
	nifES: "Inserisci un NIF valido.",
	nieES: "Inserisci un NIE valido.",
	cifES: "Inserisci un CIF valido."
});

}));