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
 * Locale: MY (Malay; Melayu)
 */
$.extend($.validator.messages, {
	required: "Medan ini diperlukan.",
	remote: "Sila betulkan medan ini.",
	email: "Sila masukkan alamat emel yang betul.",
	url: "Sila masukkan URL yang betul.",
	date: "Sila masukkan tarikh yang betul.",
	dateISO: "Sila masukkan tarikh(ISO) yang betul.",
	number: "Sila masukkan nombor yang betul.",
	digits: "Sila masukkan nilai digit sahaja.",
	creditcard: "Sila masukkan nombor kredit kad yang betul.",
	equalTo: "Sila masukkan nilai yang sama semula.",
	extension: "Sila masukkan nilai yang telah diterima.",
	maxlength: $.validator.format("Sila masukkan nilai tidak lebih dari {0} aksara."),
	minlength: $.validator.format("Sila masukkan nilai sekurang-kurangnya {0} aksara."),
	rangelength: $.validator.format("Sila masukkan panjang nilai antara {0} dan {1} aksara."),
	range: $.validator.format("Sila masukkan nilai antara {0} dan {1} aksara."),
	max: $.validator.format("Sila masukkan nilai yang kurang atau sama dengan {0}."),
	min: $.validator.format("Sila masukkan nilai yang lebih atau sama dengan {0}.")
});

}));