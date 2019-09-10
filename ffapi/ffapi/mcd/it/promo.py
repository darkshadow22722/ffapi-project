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
import logging
import os
import random
from datetime import datetime

import requests

from ffapi import utils

check_invoice = 'https://mcdonalds.fast-insight.com/voc/bs/api/v3/it/checkInvoice'

# Load locations
_locations_filename = 'locations.json'
if os.path.isfile(os.path.join(os.path.dirname(__file__), _locations_filename)):
    # __logger__.debug('Loading "{}"...'.format(__locations_filename))
    with open(os.path.join(os.path.dirname(__file__), _locations_filename)) as f:
        locations = json.loads(f.read())
else:
    # __logger__.warning('"{}" not found. Most promocodes won\'t be valid.'.format(__locations_filename))
    pass


def parse_date(d):
    return d.strftime('%Y-%m-%d')


def days_diff(d1):
    d1 = datetime.strptime(d1, "%Y-%m-%d")
    d2 = datetime.strptime('2014-12-31', "%Y-%m-%d")
    return abs((d2 - d1).days)


def flip(string):
    temp = ''
    for x in range(len(string)):
        temp = string[x] + temp
    return temp


def decimal_to_base28(src):
    dictionary = 'ABCDEFGHIJKLMNOPRSTUWXZ45679'
    return utils.decimal_to_base(dictionary, src)


def base28_to_decimal(src):
    dictionary = 'ABCDEFGHIJKLMNOPRSTUWXZ45679'
    return utils.base_to_decimal(dictionary, src)


def right(src, n):
    if n <= 0:
        return ''
    elif n > len(src):
        return src
    else:
        return src[(len(src) - n):len(src)]


def checksum(src):
    total = 0
    for i in range(len(src)):
        total = total + ord(src[i]) * (3 ** (len(src) - i))

    result = right('AAA' + decimal_to_base28(total), 3)

    return result


def insert_at(src, ins, ind):
    return src[:ind] + ins + src[ind:]


def generate_promocode(pos, site, date, num_trans):
    gen = '{:04}{:02}{:04}{:04}'.format(site, pos, days_diff(date), num_trans)
    gen2 = flip(gen)
    gen3 = '1' + gen2
    gen4 = decimal_to_base28(gen3)
    gen5 = checksum(gen4)
    gen6 = gen4
    gen6 = insert_at(gen6, gen5[0], 9)
    gen6 = insert_at(gen6, gen5[1], 6)
    gen6 = insert_at(gen6, gen5[2], 1)

    return gen6


def generate_random_promocode():
    if 'locations' in globals():
        location = int(random.choice(locations))
    else:
        location = random.randint(1, 3240)

    data = {
        'pos': random.randint(0, 99),
        'site': location,
        'date': parse_date(datetime.now()),
        'num_trans': random.randint(0, 9999)
    }

    return generate_promocode(data['pos'], data['site'], data['date'], data['num_trans'])


def generate_survey_link(promocode, proxies=None, verify=True):
    data = {
        'invoice': promocode,
        'meta': {}
    }

    x = requests.post(check_invoice, json=data, proxies=proxies, verify=verify)

    if x.status_code == 200:
        y = json.loads(x.content.decode())
        return '{url}?lang=it&timestamp={timestamp}&mbunq={mbunq}'\
            .format(url=y['data']['meta']['url'], timestamp=y['data']['meta']['timestamp'], mbunq=y['data']['data'])
    else:
        return None
