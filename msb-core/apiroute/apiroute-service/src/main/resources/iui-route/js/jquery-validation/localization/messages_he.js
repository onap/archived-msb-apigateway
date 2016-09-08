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
 * Locale: HE (Hebrew; עברית)
 */
$.extend($.validator.messages, {
	required: "השדה הזה הינו שדה חובה",
	remote: "נא לתקן שדה זה",
	email: "נא למלא כתובת דוא\"ל חוקית",
	url: "נא למלא כתובת אינטרנט חוקית",
	date: "נא למלא תאריך חוקי",
	dateISO: "נא למלא תאריך חוקי (ISO)",
	number: "נא למלא מספר",
	digits: "נא למלא רק מספרים",
	creditcard: "נא למלא מספר כרטיס אשראי חוקי",
	equalTo: "נא למלא את אותו ערך שוב",
	extension: "נא למלא ערך עם סיומת חוקית",
	maxlength: $.validator.format(".נא לא למלא יותר מ- {0} תווים"),
	minlength: $.validator.format("נא למלא לפחות {0} תווים"),
	rangelength: $.validator.format("נא למלא ערך בין {0} ל- {1} תווים"),
	range: $.validator.format("נא למלא ערך בין {0} ל- {1}"),
	max: $.validator.format("נא למלא ערך קטן או שווה ל- {0}"),
	min: $.validator.format("נא למלא ערך גדול או שווה ל- {0}")
});

}));