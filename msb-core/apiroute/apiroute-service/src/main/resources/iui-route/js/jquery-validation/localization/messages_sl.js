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
 * Language: SL (Slovenian; slovenski jezik)
 */
$.extend($.validator.messages, {
	required: "To polje je obvezno.",
	remote: "Prosimo popravite to polje.",
	email: "Prosimo vnesite veljaven email naslov.",
	url: "Prosimo vnesite veljaven URL naslov.",
	date: "Prosimo vnesite veljaven datum.",
	dateISO: "Prosimo vnesite veljaven ISO datum.",
	number: "Prosimo vnesite veljavno število.",
	digits: "Prosimo vnesite samo števila.",
	creditcard: "Prosimo vnesite veljavno številko kreditne kartice.",
	equalTo: "Prosimo ponovno vnesite vrednost.",
	extension: "Prosimo vnesite vrednost z veljavno končnico.",
	maxlength: $.validator.format("Prosimo vnesite največ {0} znakov."),
	minlength: $.validator.format("Prosimo vnesite najmanj {0} znakov."),
	rangelength: $.validator.format("Prosimo vnesite najmanj {0} in največ {1} znakov."),
	range: $.validator.format("Prosimo vnesite vrednost med {0} in {1}."),
	max: $.validator.format("Prosimo vnesite vrednost manjše ali enako {0}."),
	min: $.validator.format("Prosimo vnesite vrednost večje ali enako {0}.")
});

}));