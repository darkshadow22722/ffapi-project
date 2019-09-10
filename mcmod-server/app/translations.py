import json
import logging
import os

from app import utils
from . import config


def available_languages():
    languages = []

    # Create folder if doesn't exists
    utils.check_folder(config.translations_folder)

    # Check if folder is empty
    if not os.listdir(config.translations_folder):
        _logger.error('No translations found.')
        exit(1)

    for x in os.listdir(config.translations_folder):
        languages.append(os.path.splitext(x)[0])

    return languages


def get_string(name, language):
    x = os.path.join(config.translations_folder, language + '.json')

    # Check if translation exists
    if os.path.isfile(x):
        with open(x) as y:
            return json.loads(y.read())[name]
    else:
        # Fallback on english
        english = os.path.join(config.translations_folder, 'en.json')
        with open(english) as y:
            return json.loads(y.read())[name]


_logger = logging.getLogger(__name__)

strings = {}
_logger.info('Loading translations...')
for x in available_languages():
    target_file = os.path.join(config.translations_folder, x + '.json')

    # Check if translation exists
    if os.path.isfile(target_file):
        _logger.info('Loading "{}"...'.format(target_file))
        with open(target_file) as f:
            strings[x] = json.loads(f.read())
    else:
        _logger.error('"{}" not found.'.format(target_file))
        exit(1)
