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

# === ERRORS === #
# 100: Missing an argument.
# 101: Cannot create temp folder. Try again.
# 102: There was an error with the input file. Check the path.
# 103: Apktool error in decompiling.
# 104: Apktool error in recompiling.
# 105: Cannot find file to patch. Try again.
# 106: There was an error with the patch. Try again.
# 107: There was an error signing the APK. Try again.
# 108: There was an error zipaligning the APK. Try again.
# ============== #

WORKING_VERSIONS=( "3807" "3964" "4086" )

PATCH_VERSION="2.1.0"
PATCH_CODE="0210"

OUTPUT_FILENAME="mcmod_${PATCH_VERSION}_${PATCH_CODE}"

KEYSTORE="mcmod.keystore"
KEYSTORE_PASS="mcmod_key_password"
KEYSTORE_ALIAS="mcmod_keystore"

# =============================== #
# !!! DO NOT EDIT BEYOND THIS !!! #
# =============================== #

C_RED=$(tput setaf 9)
C_GREEN=$(tput setaf 10)
C_YELLOW=$(tput setaf 11)
C_BLUE=$(tput setaf 12)
C_RESET=$(tput sgr0)

echo "# =================================== #"
echo "| ${C_GREEN}McMod Patcher${C_RESET} - developed by Hexile |"
echo "# =================================== #"
echo "  ${C_BLUE}${PATCH_VERSION} - ${PATCH_CODE}${C_RESET}"

COMMAND_USAGE="
Usage:
./$(basename "${0}") [-h|--help] [-k|--keep-folder] [-v|--verbose] [-w|--wait] <APK_PATH> <TARGET_URL>
"

# Argument parser
POSITIONAL=()
while [[ ${#} -gt 0 ]]; do
    key="$1"

    case ${key} in
        -h|--help)
        echo "${COMMAND_USAGE}"
        exit
        ;;
        -k|--keep-folder)
        KEEP_FOLDER=true
        shift
        ;;
        -v|--verbose)
        VERBOSE=true
        shift
        ;;
        -w|--wait)
        WAIT=true
        shift
        ;;
        *)
        POSITIONAL+=("$1")
        shift
        ;;
    esac
done
set -- "${POSITIONAL[@]}"

source utils.sh

FRAMEWORK="$(pwd)/bin"
PATCH_FOLDER=$(mktemp -d tmp.patch.XXXXXXXXXX) || error "Failed to create temp folder." 101

# Add necessary tools to PATH
if [[ "$(uname)" == "Darwin" ]]; then
    export PATH="$(pwd)/bin/macos/:$PATH"
elif [[ "$(uname -s | cut -c -5)" == "Linux" ]]; then
    export PATH="$(pwd)/bin/linux/:$PATH"
fi
export PATH="$(pwd)/bin/universal/:$PATH"

# Check for commands
check_command java
check_command jarsigner
check_command sed

# Check input variables
if [[ -z "${1}" || -z "${2}" ]]; then
    error "Missing an argument.${C_RESET}${COMMAND_USAGE}" 100
fi

echo "\n${C_BLUE}File path:${C_RESET} ${1}\n${C_BLUE}Target URL:${C_RESET} ${2}"

# Check if input file exists
if [[ ! -f "${1}" ]]; then
    error "There was an error with the input file. Check the path." 102
fi

NAME=$(basename "${1}" .apk)

print_apk_info "${1}"
check_apk_version "${1}" "${WORKING_VERSIONS[@]}"

OUTPUT_FILENAME="${OUTPUT_FILENAME}_${VERSION_CODE}"

# Generate keystore
if [[ ! -f "${KEYSTORE}" ]]; then
    info "Generating keystore: ${KEYSTORE}..."
    keytool -genkeypair -alias "${KEYSTORE_ALIAS}" -keypass "${KEYSTORE_PASS}" -keystore "${KEYSTORE}" -storepass "${KEYSTORE_PASS}" -keyalg RSA -sigalg SHA1withRSA -dname "CN=mcmod,OU=mcmod,O=mcmod,L=mcmod,ST=mcmod,C=mcmod" -validity 10000
fi

if [[ "${2: -1}" != "/" ]]; then
    export TARGET_URL="${2}/"
else
    export TARGET_URL="${2}"
fi

# Decompile
info "Decompiling in ${PATCH_FOLDER}..."
apktool d -p "${FRAMEWORK}" -f "${1}" -o "${PATCH_FOLDER}" || error "There was an error decompiling the apk." 103

# Run patches
for f in $(find "patches/" -maxdepth 2 -type f ! -path "*.*" | sort -n); do source "${f}" || error "There was an error with the patch ${f}" 106; done

# Wait to recompile
if [[ "${WAIT}" == true ]]; then
    read -n 1 -s -r -p "Press any key to continue..."
    echo
fi

# Delete files that throw errors while recompiling
# In these files there are some strings malformatted.
info "Removing malformatted files..."
rm -fr "${PATCH_FOLDER}/res/values-ar-rLB/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-ar-rAE/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-sv-rSE/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-ar-rEG/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-de-rCH/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-nb-rNO/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-hr-rHR/strings.xml"
rm -fr "${PATCH_FOLDER}/res/values-de-rAT/strings.xml"

# Rebuild
info "Recompiling..."
apktool b -p "${FRAMEWORK}" "${PATCH_FOLDER}" || error "There was an error recompiling the apk." 104

# Sign and zipaling
info "Signing APK..."
jarsigner -keystore "${KEYSTORE}" -storepass "${KEYSTORE_PASS}" -keypass "${KEYSTORE_PASS}" -sigalg MD5withRSA -digestalg SHA1 -sigfile CERT -signedjar "${PATCH_FOLDER}/dist/${NAME}-patched-signed.apk" "${PATCH_FOLDER}/dist/${NAME}.apk" "${KEYSTORE_ALIAS}" || error "There was an error signing the APK." 107

info "Zipaligning APK..."
zipalign -f 4 "${PATCH_FOLDER}/dist/${NAME}-patched-signed.apk" "${OUTPUT_FILENAME}.apk" || error "There was an error zipaligning the APK." 108

# Final cleanup
if [[ "${KEEP_FOLDER}" != true ]]; then
    cleanup
fi

print_apk_info "${OUTPUT_FILENAME}.apk"

echo "${C_GREEN}Done!${C_RESET}"