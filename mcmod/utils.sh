#!/usr/bin/env bash

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

function info {
    echo "${C_BLUE}[I] ${1}${C_RESET}"
}

function verbose {
    if [[ "${VERBOSE}" == true ]]; then
        echo "${C_BLUE}[V] ${1}${C_RESET}"
    fi
}

function warning {
    echo "${C_YELLOW}[W] ${1}${C_RESET}"
}

function error {
    echo
    echo "${C_RED}[E${2}] ${1}${C_RESET}"
    cleanup
    exit "${2}"
}

function check_command {
    command -v "${1}" > /dev/null || error "This program needs \"${1}\" to run." 100
}

function check_file {
    if [[ ! -f "${1}" ]]; then
        error "File to patch not found (${1}). Try again." 105
    fi
}

function print_apk_info {
    AAPT_OUTPUT="$(aapt dump badging "${1}")"
    APP_LABEL="$(echo "${AAPT_OUTPUT}" | sed -En "s/application-label:'(.*)'/\1/p")"
    PACKAGE_NAME="$(echo "${AAPT_OUTPUT}" | sed -En "s/package: name='([^']*)'.*/\1/p")"
    VERSION_CODE="$(echo "${AAPT_OUTPUT}" | sed -En "s/.*versionCode='([^']*)'.*/\1/p")"
    VERSION_NAME="$(echo "${AAPT_OUTPUT}" | sed -En "s/.*versionName='([^']*)'.*/\1/p")"
    SUPPORTED_ARCHS="$(echo "${AAPT_OUTPUT}" | sed -En "s/native-code: (.*)/\1/p")"

    echo
    echo "${C_GREEN}Package info for ${1}:${C_RESET}"
    echo "${C_BLUE}App Label:${C_RESET} ${APP_LABEL}"
    echo "${C_BLUE}Package Name:${C_RESET} ${PACKAGE_NAME}"
    echo "${C_BLUE}Version Name:${C_RESET} ${VERSION_NAME}"
    echo "${C_BLUE}Version Code:${C_RESET} ${VERSION_CODE}"
    echo "${C_BLUE}Supported Architectures:${C_RESET} ${SUPPORTED_ARCHS}"
    echo
}

function check_apk_version {
    AAPT_OUTPUT="$(aapt dump badging "${1}")"
    VERSION_CODE="$(echo "${AAPT_OUTPUT}" | sed -En "s/.*versionCode='([^']*)'.*/\1/p")"

    echo "${C_GREEN}Checking compatibility..."
    if containsElement "${VERSION_CODE}" "${@:2}"; then
        echo "${C_BLUE}Version ${VERSION_CODE} is compatible."
    else
        echo "${C_YELLOW}WARNING: Version ${VERSION_CODE} of the app was not tested. The patch may not work."
    fi
    echo
}

function containsElement {
    local e match="$1"
    shift
    for e; do [[ "$e" == "$match" ]] && return 0; done
    return 1
}

function cleanup {
    info "Cleaning up..."
    rm -fr "${PATCH_FOLDER}"
}

function ctrlc {
    info "SIGINT received. Exiting..."
    cleanup
    exit 1
}

trap ctrlc SIGINT