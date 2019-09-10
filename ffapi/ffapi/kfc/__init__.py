# -*- coding: utf-8 -*-

#  Copyright 2019 Giacomo Ferretti
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

import requests

from . import endpoints

api_key = 'siYAzKattmaIHSwMV9OJYtaoP8SRq'

languages = {
    'it': {
        'base_assets': 'https://it.assets.kfcapi.com/',
        'market_code': 'IT',
        'country_code': 'IT',
        'country_name': 'Italia',
        'lang_code': 'it'
    },
    'de': {
        'base_assets': 'https://de.assets.kfcapi.com/',
        'market_code': 'DE',
        'country_code': 'DE',
        'country_name': 'Deutschland',
        'lang_code': 'de'
    },
    'uk': {
        'base_assets': 'https://assets.kfcapi.com/',
        'market_code': 'UK',
        'country_code': 'GB',
        'country_name': 'United Kingdom',
        'lang_code': 'en'
    },
    'es': {
        'base_assets': 'https://es.assets.kfcapi.com/',
        'market_code': 'ES',
        'country_code': 'ES',
        'country_name': 'Espa\xf1a',
        'lang_code': 'es'
    },
    'fr': {
        'base_assets': 'https://fr.assets.kfcapi.com/',
        'market_code': 'FR',
        'country_code': 'FR',
        'country_name': 'R\xe9publique fran\xe7aise',
        'lang_code': 'fr'
    },
    'nl': {
        'base_assets': 'https://nl.assets.kfcapi.com/',
        'market_code': 'NL',
        'country_code': 'NL',
        'country_name': 'Nederland',
        'lang_code': 'nl'
    }
}


class KFC:
    def __init__(self, language):
        self.language = languages[language]

        self.headers = {
            'accept': 'application/json, text/plain, */*',
            'x-api-key': api_key,
            'codemarket': self.language['market_code'],
            'marketcode': self.language['market_code'],
            'langcode': self.language['lang_code'],
            'countrycode': self.language['country_code']
        }

        self.ping = requests.Request(endpoints.ping.method, endpoints.ping.url, headers=self.headers)

        self.features = requests.Request(endpoints.features.method, endpoints.features.url, headers=self.headers,
                                         params=endpoints.features.params)

        home_promo_params = endpoints.assets.params
        home_promo_params['countryCode'] = self.language['country_code']
        home_promo_params['grouping'] = 'home'

        self.home_promo = requests.Request(endpoints.assets.method, endpoints.assets.url, headers=self.headers,
                                           params=home_promo_params)

        loyalty_params = endpoints.assets.params
        loyalty_params['countryCode'] = self.language['country_code']
        loyalty_params['grouping'] = 'loyalty'

        self.loyalty = requests.Request(endpoints.assets.method, endpoints.assets.url, headers=self.headers,
                                        params=loyalty_params)

