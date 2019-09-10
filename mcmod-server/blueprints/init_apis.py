import json

import requests
from flask import Blueprint, request

from app import config, utils, translations

init_apis = Blueprint('init_apis', __name__)


@init_apis.route('/api/config/v1/locations')
@init_apis.route('/api/config/v1/keys')
def locations():
    if 'key' not in request.args:
        return utils.error('Missing "key" argument.')

    DEVELOPMENT = 'ODE4NTYzODY1ODY1MDAyNTQ4Mjc2NDIyNTk4MjUwMjI6c3FqNnE3c2pqdDUwZnZ1NXRpaW1kaXJjaHduZG1jNmhjMXAxZ3oyZ3BmMnZsZmg3b2hmdDU3emowcThqNzMyMA=='
    STAGING = 'OTQ0MjIwMDEzNzUzMzk2OTY2Mzc2MTQyMzM5NjgxNDI6OXY3cHFxMXh0eTVtZnNvNXp4NXJzOXFhcTE2MWh5ZG4wN3l5OG1wcThyNXRycmo4aGJsYzEwNTB6NnFodDlvdQ=='
    PRODUCTION = 'ODI1OTAzOTI2NzcyNzcxNzcxNTI0MTA5ODk1ODA2NDc6cDFwd3hoanFiM2NiazdyMWlwdXFjeG85MjRreDN1dDQzNDBmd3hvd3pxM3F4bjlidmMzdml0bzlsa2N2NGl0bA=='

    key = request.args.get('key', PRODUCTION)

    base_url = 'https://config-api-dot-{}-euw-gmal-mcdonalds.appspot.com'
    if key == DEVELOPMENT:
        url = base_url.format('dev') + request.path
    elif key == STAGING:
        url = base_url.format('stg') + request.path
    elif key == PRODUCTION:
        url = base_url.format('prd') + request.path

    params = {
        'key': key
    }

    headers = {
        'User-Agent': 'okhttp/3.12.0'
    }

    r = requests.request(request.method, url, params=params, headers=headers)

    if r.status_code != 200:
        return utils.request_error(r)

    x = json.loads(r.content.decode())

    return utils.json_response(x)


@init_apis.route('/api/locationfinder/v1/client/location/info')
def locationfinder():
    if 'key' not in request.args:
        return utils.error('Missing "key" argument.')

    DEVELOPMENT = 'NjE3MjgxMjMwNDY3MDY5NTcwMTMwNTQwMjExODY1NDQ6YTNzdWsxcGsxNW1peHdybnBtZ2pwdXNmODMyNzNhOGgwbXR4ejk3NmU2b29iam1xNzV4Nmppb21panE2eWljcg=='
    STAGING = 'NzUyMzE2NzQyMjI0NjUyMTY0NDQ2NDI4NjI5ODA1NTE6cGE5ZTdlOHYyeGN0bWo2Nml1NGhldjMzbGNhajA1czJlMnF5c3RkaXAwaWRnNTRrdnc5eDllcnBmeWR2c3E2cw=='
    PRODUCTION = 'MDAwNzc3Mzg4MTg5MDI0OTM5NzI1MjE4OTA5MTgyNDY6bWE4bDNjeTh6cmkydHNnMWZicjMwMGpiYWY5NXZ1aTFpc2hmNW0xdTBsNzRlcXZraHVncmEwaTJ1aGQ4amlqZA=='

    key = request.args.get('key', PRODUCTION)
    base_url = 'https://locationfinder-api-dot-{}-euw-gmal-mcdonalds.appspot.com'
    if key == DEVELOPMENT:
        url = base_url.format('dev') + request.path
    elif key == STAGING:
        url = base_url.format('stg') + request.path
    elif key == PRODUCTION:
        url = base_url.format('prd') + request.path

    params = {
        'key': key
    }

    headers = {
        'User-Agent': 'okhttp/3.12.0'
    }

    location = {
        'country': {
            'name': 'Italy',
            'code': 'IT'
        },
        'location': {
            'latitude': 0.0,
            'longitude': 0.0
        }
    }

    return utils.json_response(location)


@init_apis.route('/api/config/v1/configs/<market_id>/<language_code>-<country_code>')
def api_config_configs(market_id, language_code, country_code):
    DEVELOPMENT = 'ODE4NTYzODY1ODY1MDAyNTQ4Mjc2NDIyNTk4MjUwMjI6c3FqNnE3c2pqdDUwZnZ1NXRpaW1kaXJjaHduZG1jNmhjMXAxZ3oyZ3BmMnZsZmg3b2hmdDU3emowcThqNzMyMA=='
    STAGING = 'OTQ0MjIwMDEzNzUzMzk2OTY2Mzc2MTQyMzM5NjgxNDI6OXY3cHFxMXh0eTVtZnNvNXp4NXJzOXFhcTE2MWh5ZG4wN3l5OG1wcThyNXRycmo4aGJsYzEwNTB6NnFodDlvdQ=='
    PRODUCTION = 'ODI1OTAzOTI2NzcyNzcxNzcxNTI0MTA5ODk1ODA2NDc6cDFwd3hoanFiM2NiazdyMWlwdXFjeG85MjRreDN1dDQzNDBmd3hvd3pxM3F4bjlidmMzdml0bzlsa2N2NGl0bA=='

    key = request.args.get('key', PRODUCTION)
    base_url = 'https://config-api-dot-{}-euw-gmal-mcdonalds.appspot.com'
    if key == DEVELOPMENT:
        url = base_url.format('dev') + request.path
    elif key == STAGING:
        url = base_url.format('stg') + request.path
    elif key == PRODUCTION:
        url = base_url.format('prd') + request.path

    params = {
        'key': key
    }

    headers = {
        'User-Agent': 'okhttp/3.12.0'
    }

    r = requests.request(request.method, url, params=params, headers=headers)

    if r.status_code != 200:
        utils.request_error(r)

    x = json.loads(r.content.decode())

    # Change siteId
    x['connectors']['vMob']['siteId'] = config.server_name

    # Disable analytics
    if 'analytic' in x:
        del x['analytic']

    # Disable forceUpdate
    x['forceUpdate']['enabled'] = False

    # Enable numericCode
    x['loyalty']['enableNumericCode'] = True

    # Change offers tutorial
    x['loyalty']['onBoardingSlides'] = [{
        'image': '{}/images/onboarding_offer.png'.format(config.server_external_url),
        'title': 'Offerte Illimitate\n@Hexile_0',
        'message': 'Per sbloccare basta fare il login lasciando vuoti i campi e cliccando su Accedi.',
        'nextButtonText': 'gmal_tutorial_done'
    }]

    # Add on boarding slides
    x['onBoarding'] = {
        'skipButtonEnalbed': True,
        'slides': [
            {
                'image': '{}/images/onboarding_mcmod.png'.format(config.server_external_url),
                'title': 'Benvenuto in McMod!',
                'message': 'McMod è l\'app moddata del McDonald\'s.\n\n'
                           'Puoi leggere il codice sorgente del custom server e della patch su '
                           '<a href="https://github.com/giacomoferretti/ffapi-project/">GitHub</a>.\n\n'
                           '<b>Se l\'app ti è utile ricordati che puoi donarmi un caffè su '
                           '<a href="https://paypal.me/hexile0">PayPal</a></b>!\n\n'
                           '<i>Author: Hexile</i>',
                'nextButtonText': 'Ok'
            }
        ]
    }

    # Change menu
    custom_menu = [
        {
            "title": "Checkout the mod on Github",
            "image": "custom_icon_github",
            "link": "https://www.github.com/giacomoferretti/mcdapi-app-mod"
        },
        {
            "title": "Donate",
            "image": "custom_icon_donate",
            "link": "https://paypal.me/hexile0"
        },
        {
            "title": "Join the Telegram channel for updates",
            "image": "icon_menu_about_italic",
            "link": "https://t.me/ffcoupons_updates"
        }
    ]

    c = 0
    for advert in custom_menu:
        x['menu']['sub'].insert(c, advert)
        c += 1

    # Change login
    x['account']['termsConsent'] = 'mcmod-consent'
    x['account']['fields'] = [
        {
            'type': 'firstName',
            'showInAccount': False,
            'required': False
        },
        {
            'type': 'email',
            'showInAccount': False,
            'required': False
        }
    ]

    # Disable email verification for countries asking for it
    if 'emailVerification' in x['account']:
        del x['account']['emailVerification']

    # Enable redeem button
    if 'hideRedeemButton' in x['loyalty']:
        del x['loyalty']['hideRedeemButton']

    # Disable account migration prompt
    if 'migrationType' in x['account']:
        del x['account']['migrationType']

    # Disable security checks
    if 'system' in x:
        del x['system']

    return utils.json_response(x)


@init_apis.route('/<language>-<country>.json')
def language_strings(language, country):
    r = requests.get('https://storage.googleapis.com/prd-euw-gmalstring-mcdonalds' + request.path)

    if r.status_code != 200:
        return utils.request_error(r)

    x = json.loads(r.content.decode())

    # Customize strings
    x['gmal_error_general_title'] = translations.get_string('generic_error_title', language)
    x['gmal_error_general_body'] = translations.get_string('generic_error_body', language)

    return utils.json_response(x)
