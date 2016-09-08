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
 * Locale: PL (Polish; język polski, polszczyzna)
 */
$.extend($.validator.messages, {
	required: "To pole jest wymagane.",
	remote: "Proszę o wypełnienie tego pola.",
	email: "Proszę o podanie prawidłowego adresu email.",
	url: "Proszę o podanie prawidłowego URL.",
	date: "Proszę o podanie prawidłowej daty.",
	dateISO: "Proszę o podanie prawidłowej daty (ISO).",
	number: "Proszę o podanie prawidłowej liczby.",
	digits: "Proszę o podanie samych cyfr.",
	creditcard: "Proszę o podanie prawidłowej karty kredytowej.",
	equalTo: "Proszę o podanie tej samej wartości ponownie.",
	extension: "Proszę o podanie wartości z prawidłowym rozszerzeniem.",
	maxlength: $.validator.format("Proszę o podanie nie więcej niż {0} znaków."),
	minlength: $.validator.format("Proszę o podanie przynajmniej {0} znaków."),
	rangelength: $.validator.format("Proszę o podanie wartości o długości od {0} do {1} znaków."),
	range: $.validator.format("Proszę o podanie wartości z przedziału od {0} do {1}."),
	max: $.validator.format("Proszę o podanie wartości mniejszej bądź równej {0}."),
	min: $.validator.format("Proszę o podanie wartości większej bądź równej {0}.")
});

}));