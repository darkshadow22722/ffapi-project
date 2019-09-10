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

import json
from json import JSONDecodeError

import requests

from . import endpoints

client_id = '65c97e4d-23d9-4730-b4f3-6df3955ce71d'
client_secret = '5e05244f-31b7-4363-81f3-828775f0153c'
content_type = 'application/json'

headers = {
    'content-type': content_type,
    'client-id': client_id,
    'client-secret': client_secret
}

# Simple GET requests
versions = requests.Request(endpoints.versions.method, endpoints.versions.url, headers=headers)
allergens = requests.Request(endpoints.allergens.method, endpoints.allergens.url, headers=headers)
ingredients = requests.Request(endpoints.ingredients.method, endpoints.ingredients.url, headers=headers)
categories = requests.Request(endpoints.categories.method, endpoints.categories.url, headers=headers)
products = requests.Request(endpoints.products.method, endpoints.products.url, headers=headers)
store_news = requests.Request(endpoints.store_news.method, endpoints.store_news.url, headers=headers)
stores = requests.Request(endpoints.stores.method, endpoints.stores.url, headers=headers)
birthdays = requests.Request(endpoints.birthdays.method, endpoints.birthdays.url, headers=headers)
providers = requests.Request(endpoints.providers.method, endpoints.providers.url, headers=headers)
faq = requests.Request(endpoints.faq.method, endpoints.faq.url, headers=headers)
heroes = requests.Request(endpoints.heroes.method, endpoints.heroes.url, headers=headers)
news = requests.Request(endpoints.news.method, endpoints.news.url, headers=headers)
terms = requests.Request(endpoints.terms.method, endpoints.terms.url, headers=headers)
coupons = requests.Request(endpoints.coupons.method, endpoints.coupons.url, headers=headers,
                           params=endpoints.coupons.params)


def login(email, password):
    return requests.Request(endpoints.login.method, endpoints.login.url, headers=headers,
                            data=endpoints.login.data_format.format(email=email, password=password))


def register(email, password, first_name, last_name, birthday='1990-01-01', referral_code=''):
    return requests.Request(endpoints.register.method, endpoints.register.url, headers=headers,
                            data=endpoints.register.data_format.format(first_name=first_name, last_name=last_name,
                                                                       birthday=birthday, email=email,
                                                                       password=password, referral_code=referral_code))


def get_access_token(content):
    try:
        content = content.decode()
    except AttributeError:
        pass

    try:
        return json.loads(content)['user']['access_token']
    except (JSONDecodeError, KeyError):
        return None
