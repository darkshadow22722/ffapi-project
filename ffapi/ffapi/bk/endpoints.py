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

base = 'https://api.burgerking.it/'
base_assets = 'http://www.burgerking.it/site/assets/files/'
api = 'api/v1/'
api_v2 = 'api/v2/'

# Versions
VERSIONS = {
    'method': 'GET',
    'url': base + api + 'versions'
}
versions = SimpleNamespace(**VERSIONS)

# Allergens
ALLERGENS = {
    'method': 'GET',
    'url': base + api + 'allergens'
}
allergens = SimpleNamespace(**ALLERGENS)

# Ingredients
INGREDIENTS = {
    'method': 'GET',
    'url': base + api + 'ingredients'
}
ingredients = SimpleNamespace(**INGREDIENTS)

# Categories
CATEGORIES = {
    'method': 'GET',
    'url': base + api + 'categories'
}
categories = SimpleNamespace(**CATEGORIES)

# Products
PRODUCTS = {
    'method': 'GET',
    'url': base + api + 'products'
}
products = SimpleNamespace(**PRODUCTS)

# Store news
STORE_NEWS = {
    'method': 'GET',
    'url': base + api + 'store_news'
}
store_news = SimpleNamespace(**STORE_NEWS)

# Stores
STORES = {
    'method': 'GET',
    'url': base + api + 'stores'
}
stores = SimpleNamespace(**STORES)

# Birthdays
BIRTHDAYS = {
    'method': 'GET',
    'url': base + api + 'birthdays'
}
birthdays = SimpleNamespace(**BIRTHDAYS)

# Providers
PROVIDERS = {
    'method': 'GET',
    'url': base + api + 'providers'
}
providers = SimpleNamespace(**PROVIDERS)

# FAQ
FAQ = {
    'method': 'GET',
    'url': base + api + 'faq'
}
faq = SimpleNamespace(**FAQ)

# Heroes
HEROES = {
    'method': 'GET',
    'url': base + api + 'heroes'
}
heroes = SimpleNamespace(**HEROES)

# News
NEWS = {
    'method': 'GET',
    'url': base + api + 'news'
}
news = SimpleNamespace(**NEWS)

# Coupons
COUPONS = {
    'method': 'GET',
    'url': base + api + 'coupons',
    'params': {
        'cache': False
    }
}
coupons = SimpleNamespace(**COUPONS)

# Terms
TERMS = {
    'method': 'GET',
    'url': base + api + 'terms'
}
terms = SimpleNamespace(**TERMS)

# Login
LOGIN = {
    'method': 'POST',
    'url': base + api_v2 + 'login',
    'data_dict': {
        'username': '',
        'password': ''
    },
    'data_format': '{{"username":"{email}","password":"{password}"}}'
}
login = SimpleNamespace(**LOGIN)

# Logout
LOGOUT = {
    'method': 'GET',
    'url': base + api + 'logout',
    'needed_headers': [
        'authorization'
    ]
}
logout = SimpleNamespace(**LOGOUT)

# Register
REGISTER = {
    'method': 'POST',
    'url': base + api_v2 + 'register',
    'data_dict': {
        'first_name': '',
        'last_name': '',
        'birthday': '',
        'username': '',
        'password': '',
        'confirm_password': '',
        'referral_code': '',
        'privacies': ['privacy-terms'],
        'notifications': ['notification-push']
    },
    'data_format': '{{"first_name":"{first_name}","last_name":"{last_name}","birthday":"{birthday}","username":"{'
                   'email}","password":"{password}","confirm_password":"{password}","referral_code":"{'
                   'referral_code}","privacies":["privacy-terms"],"notifications":["notification-push"]}}'
}
register = SimpleNamespace(**REGISTER)

# User
USER = {
    'method': 'GET',
    'url': base + api + 'user',
    'params': {
        'loyalty': True
    },
    'needed_headers': [
        'authorization'
    ]
}
user = SimpleNamespace(**USER)

# User QR Code
QR_CODE = {
    'method': 'GET',
    'url': base + api + 'qrcode',
    'needed_headers': [
        'authorization'
    ]
}
qr_code = SimpleNamespace(**QR_CODE)

# Modify User
MODIFY = {
    'method': 'POST',
    'url': base + api + 'modify',
    'needed_headers': [
        'authorization'
    ],
    'data_dict': {
        'notifications': ["notification-push"]
    },
    'data_format': '{"notifications":["notification-push"]}'
}
modify = SimpleNamespace(**MODIFY)

# Send Reset Password
SEND_RESET_PASSWORD = {
    'method': 'POST',
    'url': base + api_v2 + 'sendResetPassword',
    'data_dict': {
        'email': ''
    },
    'data_format': '{{"email":"{email}"}}'
}
send_reset_password = SimpleNamespace(**SEND_RESET_PASSWORD)
