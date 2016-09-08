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
 * Locale: KA (Georgian; ქართული)
 */
$.extend($.validator.messages, {
	required: "ამ ველის შევსება აუცილებელია.",
	remote: "გთხოვთ მიუთითოთ სწორი მნიშვნელობა.",
	email: "გთხოვთ მიუთითოთ ელ-ფოსტის კორექტული მისამართი.",
	url: "გთხოვთ მიუთითოთ კორექტული URL.",
	date: "გთხოვთ მიუთითოთ კორექტული თარიღი.",
	dateISO: "გთხოვთ მიუთითოთ კორექტული თარიღი ISO ფორმატში.",
	number: "გთხოვთ მიუთითოთ ციფრი.",
	digits: "გთხოვთ მიუთითოთ მხოლოდ ციფრები.",
	creditcard: "გთხოვთ მიუთითოთ საკრედიტო ბარათის კორექტული ნომერი.",
	equalTo: "გთხოვთ მიუთითოთ ასეთივე მნიშვნელობა კიდევ ერთხელ.",
	extension: "გთხოვთ აირჩიოთ ფაილი კორექტული გაფართოებით.",
	maxlength: $.validator.format("დასაშვებია არაუმეტეს {0} სიმბოლო."),
	minlength: $.validator.format("აუცილებელია შეიყვანოთ მინიმუმ {0} სიმბოლო."),
	rangelength: $.validator.format("ტექსტში სიმბოლოების რაოდენობა უნდა იყოს {0}-დან {1}-მდე."),
	range: $.validator.format("გთხოვთ შეიყვანოთ ციფრი {0}-დან {1}-მდე."),
	max: $.validator.format("გთხოვთ შეიყვანოთ ციფრი რომელიც ნაკლებია ან უდრის {0}-ს."),
	min: $.validator.format("გთხოვთ შეიყვანოთ ციფრი რომელიც მეტია ან უდრის {0}-ს.")
});

}));