import json
import logging
import os

from app import logger, utils

config_file = 'config.json'

# Setup logger
_root_logger = logging.getLogger()
_root_logger.setLevel(level=logging.DEBUG)
_log_format = '[%(asctime)s] %(levelname)s/%(module)s: %(message)s'
_log_format_date = '%Y-%m-%d %H:%M:%S'
_console_out_handler = logger.console_handler(logging.INFO, _log_format, _log_format_date, True)
_console_err_handler = logger.console_handler(logging.WARNING, _log_format, _log_format_date)
_root_logger.addHandler(_console_out_handler)
_root_logger.addHandler(_console_err_handler)
_logger = logging.getLogger(__name__)

if os.path.isfile(config_file):
    _logger.info('Loading "{}"...'.format(config_file))
    with open(config_file) as f:
        _config = json.loads(f.read())
else:
    _logger.error('"{}" not found.'.format(config_file))
    exit(1)

server_name = _config['server']['name']
server_version = _config['server']['version']
server_debug = _config['server']['debug']
server_host = _config['server']['host']
server_port = _config['server']['port']
server_external_url = _config['server']['externalUrl']

override_assets = _config['overrideAssets']

token_factory_post_keys = _config['tokenFactory']['postKeys']
token_factory_get_keys = _config['tokenFactory']['getKeys']

translations_folder = _config['translationsFolder']

storage_type = _config['storage']['type']
storage_json_folder = _config['storage']['json']['folder']
storage_json_file = _config['storage']['json']['file']
storage_redis_url = _config['storage']['redis']['url']
storage_redis_port = _config['storage']['redis']['port']
storage_redis_username = _config['storage']['redis']['username']
storage_redis_password = _config['storage']['redis']['password']

logging_level = _config['logging']['level'].upper()
logging_format = _config['logging']['format']
logging_date_format = _config['logging']['dateFormat']
logging_folder = _config['logging']['folder']
logging_file = _config['logging']['file']
logging_separate = _config['logging']['separateErrors']
logging_error_file = _config['logging']['errorFile']
logging_compress = _config['logging']['compress']
logging_suffix = _config['logging']['suffix']
logging_when = _config['logging']['when']
logging_interval = _config['logging']['interval']

# Remove default logger
_root_logger.removeHandler(_console_out_handler)
_root_logger.removeHandler(_console_err_handler)

# Console logging
if logging_level == 'INFO' or logging_level == 'DEBUG':
    _root_logger.addHandler(logger.console_handler(logging.getLevelName(logging_level), log_format=logging_format,
                                                   log_date_format=logging_date_format, log_filter=True))
_root_logger.addHandler(logger.console_handler(logging.WARNING, log_format=logging_format,
                                               log_date_format=logging_date_format))

# File logging
utils.check_folder(logging_folder)
logging_file_path = os.path.join(logging_folder, logging_file)
if logging_separate:
    _root_logger.addHandler(
        logger.timed_handler(logging.getLevelName(logging_level), logging_file_path, log_format=logging_format,
                             log_filter=True, log_date_format=logging_date_format, compress=logging_compress,
                             suffix=logging_suffix, when=logging_when, interval=logging_interval))

    logging_error_file_path = os.path.join(logging_folder, logging_error_file)
    _root_logger.addHandler(logger.timed_handler(logging.WARNING, logging_error_file_path, log_format=logging_format,
                                                 log_date_format=logging_date_format, compress=logging_compress,
                                                 suffix=logging_suffix, when=logging_when, interval=logging_interval))
else:
    _root_logger.addHandler(logger.timed_handler(logging.getLevelName(logging_level), logging_file_path,
                                                 log_format=logging_format, log_date_format=logging_date_format,
                                                 compress=logging_compress, suffix=logging_suffix, when=logging_when,
                                                 interval=logging_interval))