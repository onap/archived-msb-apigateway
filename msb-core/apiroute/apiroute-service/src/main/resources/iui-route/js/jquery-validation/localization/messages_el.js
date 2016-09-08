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
 * Locale: EL (Greek; ελληνικά)
 */
$.extend($.validator.messages, {
	required: "Αυτό το πεδίο είναι υποχρεωτικό.",
	remote: "Παρακαλώ διορθώστε αυτό το πεδίο.",
	email: "Παρακαλώ εισάγετε μια έγκυρη διεύθυνση email.",
	url: "Παρακαλώ εισάγετε ένα έγκυρο URL.",
	date: "Παρακαλώ εισάγετε μια έγκυρη ημερομηνία.",
	dateISO: "Παρακαλώ εισάγετε μια έγκυρη ημερομηνία (ISO).",
	number: "Παρακαλώ εισάγετε έναν έγκυρο αριθμό.",
	digits: "Παρακαλώ εισάγετε μόνο αριθμητικά ψηφία.",
	creditcard: "Παρακαλώ εισάγετε έναν έγκυρο αριθμό πιστωτικής κάρτας.",
	equalTo: "Παρακαλώ εισάγετε την ίδια τιμή ξανά.",
	extension: "Παρακαλώ εισάγετε μια τιμή με έγκυρη επέκταση αρχείου.",
	maxlength: $.validator.format("Παρακαλώ εισάγετε μέχρι και {0} χαρακτήρες."),
	minlength: $.validator.format("Παρακαλώ εισάγετε τουλάχιστον {0} χαρακτήρες."),
	rangelength: $.validator.format("Παρακαλώ εισάγετε μια τιμή με μήκος μεταξύ {0} και {1} χαρακτήρων."),
	range: $.validator.format("Παρακαλώ εισάγετε μια τιμή μεταξύ {0} και {1}."),
	max: $.validator.format("Παρακαλώ εισάγετε μια τιμή μικρότερη ή ίση του {0}."),
	min: $.validator.format("Παρακαλώ εισάγετε μια τιμή μεγαλύτερη ή ίση του {0}.")
});

}));