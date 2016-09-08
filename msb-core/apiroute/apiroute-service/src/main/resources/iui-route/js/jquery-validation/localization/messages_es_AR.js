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
 * Locale: ES (Spanish; Español)
 * Region: AR (Argentina)
 */
$.extend($.validator.messages, {
	required: "Este campo es obligatorio.",
	remote: "Por favor, completá este campo.",
	email: "Por favor, escribí una dirección de correo válida.",
	url: "Por favor, escribí una URL válida.",
	date: "Por favor, escribí una fecha válida.",
	dateISO: "Por favor, escribí una fecha (ISO) válida.",
	number: "Por favor, escribí un número entero válido.",
	digits: "Por favor, escribí sólo dígitos.",
	creditcard: "Por favor, escribí un número de tarjeta válido.",
	equalTo: "Por favor, escribí el mismo valor de nuevo.",
	extension: "Por favor, escribí un valor con una extensión aceptada.",
	maxlength: $.validator.format("Por favor, no escribas más de {0} caracteres."),
	minlength: $.validator.format("Por favor, no escribas menos de {0} caracteres."),
	rangelength: $.validator.format("Por favor, escribí un valor entre {0} y {1} caracteres."),
	range: $.validator.format("Por favor, escribí un valor entre {0} y {1}."),
	max: $.validator.format("Por favor, escribí un valor menor o igual a {0}."),
	min: $.validator.format("Por favor, escribí un valor mayor o igual a {0}."),
	nifES: "Por favor, escribí un NIF válido.",
	nieES: "Por favor, escribí un NIE válido.",
	cifES: "Por favor, escribí un CIF válido."
});

}));