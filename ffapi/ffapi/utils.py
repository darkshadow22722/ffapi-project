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

import random
import string


def pad(src, n):
    return '{src:0{n}}'.format(src=src, n=n)


def decimal_to_base(dictionary, src):
    base = len(dictionary)

    result = ''
    value = int(src)

    while value > 0:
        remainder = int(value % base)
        value = int((value - remainder) / base)
        result = dictionary[remainder] + result

    return result


def base_to_decimal(dictionary, src):
    base = len(dictionary)

    value = 0
    posizione = len(src)

    for i in range(len(src)):
        valpos = dictionary.index(src[i])
        if valpos < 0 or valpos > base - 1:
            return value
        posizione -= 1
        value = value + valpos * (base ** posizione)

    return value


def generate_random(dictionary, length=10):
    return ''.join(random.choice(dictionary) for i in range(length))


def random_string(length=10):
    return generate_random(string.ascii_lowercase, length)


def random_numbers(length=10):
    return generate_random(string.digits, length)


def random_version(length=3):
    version = ''
    for x in range(length):
        version += str(random.randint(0, 9))
        if length > 1:
            version += '.'
            length -= 1

    return version
