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
import os
import random
from datetime import datetime

import requests

from ffapi import utils

check_invoice = 'https://mcdonalds.fast-insight.com/voc/bs/api/spain/checkInvoice'
single_store = 'https://voice.fast-insight.com/api/v1/admin/referral/single-store.php'

# Load locations
_locations_filename = 'locations.json'
if os.path.isfile(os.path.join(os.path.dirname(__file__), _locations_filename)):
    # __logger__.debug('Loading "{}"...'.format(__locations_filename))
    with open(os.path.join(os.path.dirname(__file__), _locations_filename)) as f:
        locations = json.loads(f.read())
else:
    # __logger__.warning('"{}" not found. Most promocodes won\'t be valid.'.format(__locations_filename))
    pass


def decimal_to_base25(src):
    dictionary = 'CM7WD6N4RHF9ZL3XKQGVPBTJY'
    return utils.decimal_to_base(dictionary, src)


def base25_to_decimal(src):
    dictionary = 'CM7WD6N4RHF9ZL3XKQGVPBTJY'
    return utils.base_to_decimal(dictionary, src)


def generate_promocode(store_id, month, day, hour, minute, pos_id, order_id, amount):
    store_id = decimal_to_base25(store_id)
    survey_time = decimal_to_base25('19{:02}{:02}{:02}{:02}'.format(month, day, hour, minute))
    pos_id = decimal_to_base25('{:02}'.format(pos_id))
    order_id = decimal_to_base25('{:02}'.format(order_id))
    amount = decimal_to_base25('{:02}'.format(amount))

    return '{:C>3}{:C>7}{:C>2}{:C>2}{:C>3}C'.format(store_id, survey_time, pos_id, order_id, amount)


def generate_random_promocode():
    if 'locations' in globals():
        location = int(random.choice(locations))
    else:
        location = random.randint(1, 9999)

    date = datetime.now()

    return generate_promocode(location, date.month, date.day, date.hour, date.minute, random.randint(0, 41),
                              random.randint(0, 642), random.randint(0, 15624))


def generate_survey_link(promocode, proxies=None, verify=True):
    data = {
        'svid': 'AMOPO',
        'ssns': [
            {
                'id': 51,
                'value': promocode
            }
        ]
    }

    x = requests.post(single_store, json=data, proxies=proxies, verify=verify)

    if x.status_code == 200:
        y = json.loads(x.content.decode())
        return y['data']['url']
    else:
        return None
