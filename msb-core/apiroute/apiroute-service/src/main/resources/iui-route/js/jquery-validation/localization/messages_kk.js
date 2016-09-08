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
 * Locale: KK (Kazakh; қазақ тілі)
 */
$.extend($.validator.messages, {
	required: "Бұл өрісті міндетті түрде толтырыңыз.",
	remote: "Дұрыс мағына енгізуіңізді сұраймыз.",
	email: "Нақты электронды поштаңызды енгізуіңізді сұраймыз.",
	url: "Нақты URL-ды енгізуіңізді сұраймыз.",
	date: "Нақты URL-ды енгізуіңізді сұраймыз.",
	dateISO: "Нақты ISO форматымен сәйкес датасын енгізуіңізді сұраймыз.",
	number: "Күнді енгізуіңізді сұраймыз.",
	digits: "Тек қана сандарды енгізуіңізді сұраймыз.",
	creditcard: "Несие картасының нөмірін дұрыс енгізуіңізді сұраймыз.",
	equalTo: "Осы мәнді қайта енгізуіңізді сұраймыз.",
	extension: "Файлдың кеңейтуін дұрыс таңдаңыз.",
	maxlength: $.validator.format("Ұзындығы {0} символдан көр болмасын."),
	minlength: $.validator.format("Ұзындығы {0} символдан аз болмасын."),
	rangelength: $.validator.format("Ұзындығы {0}-{1} дейін мән енгізуіңізді сұраймыз."),
	range: $.validator.format("Пожалуйста, введите число от {0} до {1}. - {0} - {1} санын енгізуіңізді сұраймыз."),
	max: $.validator.format("{0} аз немесе тең санын енгізуіңіді сұраймыз."),
	min: $.validator.format("{0} көп немесе тең санын енгізуіңізді сұраймыз.")
});

}));