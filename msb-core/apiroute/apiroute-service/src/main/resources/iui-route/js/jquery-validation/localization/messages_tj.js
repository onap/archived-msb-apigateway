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
 * Locale: TJ (Tajikistan; Забони тоҷикӣ)
 */
$.extend($.validator.messages, {
	required: "Ворид кардани ин филд маҷбури аст.",
	remote: "Илтимос, маълумоти саҳеҳ ворид кунед.",
	email: "Илтимос, почтаи электронии саҳеҳ ворид кунед.",
	url: "Илтимос, URL адреси саҳеҳ ворид кунед.",
	date: "Илтимос, таърихи саҳеҳ ворид кунед.",
	dateISO: "Илтимос, таърихи саҳеҳи (ISO)ӣ ворид кунед.",
	number: "Илтимос, рақамҳои саҳеҳ ворид кунед.",
	digits: "Илтимос, танҳо рақам ворид кунед.",
	creditcard: "Илтимос, кредит карди саҳеҳ ворид кунед.",
	equalTo: "Илтимос, миқдори баробар ворид кунед.",
	extension: "Илтимос, қофияи файлро дуруст интихоб кунед",
	maxlength: $.validator.format("Илтимос, бештар аз {0} рамз ворид накунед."),
	minlength: $.validator.format("Илтимос, камтар аз {0} рамз ворид накунед."),
	rangelength: $.validator.format("Илтимос, камтар аз {0} ва зиёда аз {1} рамз ворид кунед."),
	range: $.validator.format("Илтимос, аз {0} то {1} рақам зиёд ворид кунед."),
	max: $.validator.format("Илтимос, бештар аз {0} рақам ворид накунед."),
	min: $.validator.format("Илтимос, камтар аз {0} рақам ворид накунед.")
});

}));