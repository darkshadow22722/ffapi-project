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

from types import SimpleNamespace

dev_base = 'https://dev.kfcapi.com/'
preprod_base = 'https://preprod.kfcapi.com/'

base = 'https://prod.kfcapi.com/'
api = 'api/v1/'
api_v2 = 'api/v2/'

# Ping
PING = {
    'method': 'GET',
    'url': base + api + 'ping'
}
ping = SimpleNamespace(**PING)

# Features
FEATURES = {
    'method': 'GET',
    'url': base + api + 'util/features',
    'params': {
        'type': 'mobile'
    }
}
features = SimpleNamespace(**FEATURES)

# Assets
ASSETS = {
    'method': 'GET',
    'url': base + api + 'util/assets',
    'params': {
        'countryCode': '',
        'mode': 'mobile',
        'grouping': 'loyalty',              # loyalty | home
        'modifiedSinceDate': '2018-07-10'
    }
}
assets = SimpleNamespace(**ASSETS)

# Settings
SETTINGS = {
    'method': 'GET',
    'url': base + api + 'util/settings',
    'params': {
        'type': 'mobile'
    }
}
settings = SimpleNamespace(**SETTINGS)

# Translations
TRANSLATIONS = {
    'method': 'GET',
    'url': base + api + 'util/translations',
    'params': {
        'type': 'mobile'
    }
}
translations = SimpleNamespace(**TRANSLATIONS)

# Promotions
PROMOTIONS = {
    'method': 'GET',
    'url': base + api + 'promotions'
}
promotions = SimpleNamespace(**PROMOTIONS)
