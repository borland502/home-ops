#!/usr/bin/env bash

# Define the output file path relative to the build script
if [[ $(basename "$PWD") == "home-ops" ]]; then
    cd scripts/shell || exit 2
fi

OUTPUT_FILE="../../install.sh"


# Create the output file with a shebang
echo '#!/usr/bin/env bash' > "${OUTPUT_FILE}"

# Add newline after shebang
echo '' >> "${OUTPUT_FILE}"

echo 'echo "Starting Home-Ops Installation Script${RESET} with $(whoami) in $(pwd)"' >> "${OUTPUT_FILE}"

# Concatenate the files
cat "src/default_path.sh" \
"src/install_doctor_functions.sh" \
"src/dasbootstrap.sh" \
"src/user_install.sh" \
"src/home-ops_install.sh" \
>> "${OUTPUT_FILE}"

# Make the output file executable
chmod +x "${OUTPUT_FILE}"

echo "Created ${OUTPUT_FILE}"
cd ../..
