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

import math
import random
from base64 import urlsafe_b64encode, urlsafe_b64decode
from datetime import datetime, timedelta

from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

from ffapi import utils

des_key = 'co.vmob.sdk.android.encrypt.key'


def unpad(src):
    return src[:-src[-1]]


def pad(src, block_size):
    no_of_blocks = math.ceil(len(src) / float(block_size))
    pad_value = int(no_of_blocks * block_size - len(src))

    if pad_value == 0:
        return src + chr(block_size) * block_size
    else:
        return src + chr(pad_value) * pad_value


def get_current_time():
    return (datetime.utcnow() - timedelta(minutes=10)).isoformat() + '0Z'


def encrypt_des(message):
    cipher = Cipher(algorithms.TripleDES(des_key[:8].encode()), modes.ECB(), backend=default_backend())
    encryptor = cipher.encryptor()
    return urlsafe_b64encode(encryptor.update(pad(message, 8).encode()) + encryptor.finalize()).decode() + '_'


def decrypt_des(message):
    cipher = Cipher(algorithms.TripleDES(des_key[:8].encode()), modes.ECB(), backend=default_backend())
    decryptor = cipher.decryptor()
    return unpad(decryptor.update(urlsafe_b64decode(message)) + decryptor.finalize())


def get_random_android_id():
    return '{:016x}'.format(random.randrange(16 ** 16))


def generate_username(device_id):
    return encrypt_des('DeviceUsernamePrefix{}'.format(device_id))


def generate_password(device_id):
    return encrypt_des('DevicePasswordPrefix{}'.format(device_id))


def generate_vmob_uid(device_id):
    return encrypt_des('co.vmob.android.sdk.{}'.format(device_id))


def get_headers(vmob):
    return {
        'Content-Type': 'application/json',
        'Accept-Language': 'en-US',
        'User-Agent': 'okhttp/3.12.0',
        'X-Dif-Platform': 'android',
        'x-vmob-uid': vmob,
        'x-vmob-device_os_version': '0',
        'x-vmob-device_type': 'a',
        'x-vmob-device_screen_resolution': '0x0',
        'x-vmob-device': 'android',
        'x-vmob-device_utc_offset': '+0:00',
        'x-vmob-device_network_type': 'wifi',
        'x-vmob-application_version': '3587'
    }


def get_random_headers(vmob=None):
    if vmob is None:
        vmob = generate_vmob_uid(get_random_android_id())

    headers = get_headers(vmob)

    headers['User-Agent'] = '{}/{}'.format(utils.random_string(), utils.random_version())
    headers['x-vmob-device_os_version'] = utils.random_string()
    headers['x-vmob-device_screen_resolution'] = '{}x{}'.format(random.randint(0, 100000), random.randint(0, 100000))
    headers['x-vmob-device'] = utils.random_string()
    headers['x-vmob-device_utc_offset'] = '+{}:{:02}'.format(random.randint(0, 9), random.randint(0, 59))
    headers['x-vmob-device_network_type'] = utils.random_string()
    headers['x-vmob-application_version'] = utils.random_string()

    return headers
