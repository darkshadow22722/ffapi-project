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

import os

import setuptools
from setuptools import setup

from ffapi import __version__

base_path = os.path.dirname(__file__)

# Load long description from README.md
with open(os.path.join(base_path, 'README.md')) as f:
    __long_description__ = f.read()

setup(
    name="ffapi",
    packages=setuptools.find_packages(),
    version=__version__,
    license="Apache 2.0",
    author="Giacomo Ferretti",
    author_email="me@hexile.xyz",
    description="A wrapper for many fast food servers written in Python.",
    long_description=__long_description__,
    long_description_content_type="text/markdown",
    url="https://github.com/giacomoferretti/ffapi",
    classifiers=[
        "Development Status :: 4 - Beta",
        "License :: OSI Approved :: Apache Software License",
        "Operating System :: OS Independent",
        "Programming Language :: Python",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3 :: Only",
        "Programming Language :: Python :: 3.5",
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "Topic :: Internet"
    ],
    install_requires=[
        'requests',
        'cryptography'
    ],
    package_data={'ffapi': ['mcd/es/locations.json', 'mcd/it/locations.json']},
    include_package_data=True
)
