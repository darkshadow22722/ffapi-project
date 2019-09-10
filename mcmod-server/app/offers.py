import json
import logging
import os

_logger = logging.getLogger(__name__)

offers = {}
_logger.info('Loading offers...')

# Check if offers file exists
target_file = 'offers.json'
if os.path.isfile(target_file):
    _logger.info('Loading "{}"...'.format(target_file))
    with open(target_file) as f:
        offers = json.loads(f.read())
else:
    _logger.error('"{}" not found.'.format(target_file))
    exit(1)

active_offers = []
for x in offers:
    if x['isActive'] and not x['isReward']:  # and x['image'] is not None:
        active_offers.append(x)


def get_offers(merchant_id, active=True):
    x = []

    if active:
        y = active_offers
    else:
        y = offers

    for z in y:
        if z['merchantId'] == merchant_id:
            x.append(z)

    return x
