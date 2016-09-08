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
 * Locale: GL (Galician; Galego)
 */
(function($) {
	$.extend($.validator.messages, {
		required: "Este campo é obrigatorio.",
		remote: "Por favor, cubre este campo.",
		email: "Por favor, escribe unha dirección de correo válida.",
		url: "Por favor, escribe unha URL válida.",
		date: "Por favor, escribe unha data válida.",
		dateISO: "Por favor, escribe unha data (ISO) válida.",
		number: "Por favor, escribe un número válido.",
		digits: "Por favor, escribe só díxitos.",
		creditcard: "Por favor, escribe un número de tarxeta válido.",
		equalTo: "Por favor, escribe o mesmo valor de novo.",
		extension: "Por favor, escribe un valor cunha extensión aceptada.",
		maxlength: $.validator.format("Por favor, non escribas máis de {0} caracteres."),
		minlength: $.validator.format("Por favor, non escribas menos de {0} caracteres."),
		rangelength: $.validator.format("Por favor, escribe un valor entre {0} e {1} caracteres."),
		range: $.validator.format("Por favor, escribe un valor entre {0} e {1}."),
		max: $.validator.format("Por favor, escribe un valor menor ou igual a {0}."),
		min: $.validator.format("Por favor, escribe un valor maior ou igual a {0}."),
		nifES: "Por favor, escribe un NIF válido.",
		nieES: "Por favor, escribe un NIE válido.",
		cifES: "Por favor, escribe un CIF válido."
	});
}(jQuery));

}));