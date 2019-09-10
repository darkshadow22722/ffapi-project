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

import gzip
import logging
import os
import sys
from logging.handlers import TimedRotatingFileHandler


class InfoFilter(logging.Filter):
    def filter(self, rec):
        return rec.levelno in [logging.DEBUG, logging.INFO]


def console_handler(level, log_format=None, log_date_format=None, log_filter=False):
    log_formatter = logging.Formatter(log_format, log_date_format)

    if level <= 20 and log_filter:
        handler = logging.StreamHandler(sys.stdout)
        handler.addFilter(InfoFilter())
    else:
        handler = logging.StreamHandler(sys.stderr)

    handler.setLevel(level=level)
    handler.setFormatter(log_formatter)

    return handler


def namer(name):
    return name + '.gz'


def rotator(source, dest):
    with open(source, 'rb') as sf:
        data = sf.read()
        compressed = gzip.compress(data)
        with open(dest, 'wb') as df:
            df.write(compressed)
    os.remove(source)


def timed_handler(level, path, log_format=None, log_date_format=None, log_filter=False, compress=True, suffix='%Y%m%d',
                  when='midnight', interval=1):
    log_formatter = logging.Formatter(log_format, log_date_format)

    handler = TimedRotatingFileHandler(path, when=when, interval=interval)
    handler.suffix = suffix
    handler.setLevel(level=level)
    handler.setFormatter(log_formatter)

    if compress:
        handler.rotator = rotator
        handler.namer = namer

    if level <= 20 and log_filter:
        handler.addFilter(InfoFilter())

    return handler
