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
 * Locale: SK (Slovak; slovenčina, slovenský jazyk)
 */
$.extend($.validator.messages, {
	required: "Povinné zadať.",
	maxlength: $.validator.format("Maximálne {0} znakov."),
	minlength: $.validator.format("Minimálne {0} znakov."),
	rangelength: $.validator.format("Minimálne {0} a Maximálne {1} znakov."),
	email: "E-mailová adresa musí byť platná.",
	url: "URL musí byť platný.",
	date: "Musí byť dátum.",
	number: "Musí byť číslo.",
	digits: "Môže obsahovať iba číslice.",
	equalTo: "Dva hodnoty sa musia rovnať.",
	range: $.validator.format("Musí byť medzi {0} a {1}."),
	max: $.validator.format("Nemôže byť viac ako{0}."),
	min: $.validator.format("Nemôže byť menej ako{0}."),
	creditcard: "Číslo platobnej karty musí byť platné."
});

}));