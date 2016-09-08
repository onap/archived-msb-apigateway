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
 * Locale: EU (Basque; euskara, euskera)
 */
$.extend($.validator.messages, {
	required: "Eremu hau beharrezkoa da.",
	remote: "Mesedez, bete eremu hau.",
	email: "Mesedez, idatzi baliozko posta helbide bat.",
	url: "Mesedez, idatzi baliozko URL bat.",
	date: "Mesedez, idatzi baliozko data bat.",
	dateISO: "Mesedez, idatzi baliozko (ISO) data bat.",
	number: "Mesedez, idatzi baliozko zenbaki oso bat.",
	digits: "Mesedez, idatzi digituak soilik.",
	creditcard: "Mesedez, idatzi baliozko txartel zenbaki bat.",
	equalTo: "Mesedez, idatzi berdina berriro ere.",
	extension: "Mesedez, idatzi onartutako luzapena duen balio bat.",
	maxlength: $.validator.format("Mesedez, ez idatzi {0} karaktere baino gehiago."),
	minlength: $.validator.format("Mesedez, ez idatzi {0} karaktere baino gutxiago."),
	rangelength: $.validator.format("Mesedez, idatzi {0} eta {1} karaktere arteko balio bat."),
	range: $.validator.format("Mesedez, idatzi {0} eta {1} arteko balio bat."),
	max: $.validator.format("Mesedez, idatzi {0} edo txikiagoa den balio bat."),
	min: $.validator.format("Mesedez, idatzi {0} edo handiagoa den balio bat.")
});

}));