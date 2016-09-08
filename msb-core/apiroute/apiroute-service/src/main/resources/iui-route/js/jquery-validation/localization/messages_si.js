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
 * Locale: SI (Slovenian)
 */
$.extend($.validator.messages, {
	required: "To polje je obvezno.",
	remote: "Vpis v tem polju ni v pravi obliki.",
	email: "Prosimo, vnesite pravi email naslov.",
	url: "Prosimo, vnesite pravi URL.",
	date: "Prosimo, vnesite pravi datum.",
	dateISO: "Prosimo, vnesite pravi datum (ISO).",
	number: "Prosimo, vnesite pravo številko.",
	digits: "Prosimo, vnesite samo številke.",
	creditcard: "Prosimo, vnesite pravo številko kreditne kartice.",
	equalTo: "Prosimo, ponovno vnesite enako vsebino.",
	extension: "Prosimo, vnesite vsebino z pravo končnico.",
	maxlength: $.validator.format("Prosimo, da ne vnašate več kot {0} znakov."),
	minlength: $.validator.format("Prosimo, vnesite vsaj {0} znakov."),
	rangelength: $.validator.format("Prosimo, vnesite od {0} do {1} znakov."),
	range: $.validator.format("Prosimo, vnesite vrednost med {0} in {1}."),
	max: $.validator.format("Prosimo, vnesite vrednost manjšo ali enako {0}."),
	min: $.validator.format("Prosimo, vnesite vrednost večjo ali enako {0}.")
});

}));