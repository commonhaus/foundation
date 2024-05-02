#!/bin/bash -x

# Docker image for pandoc
if [[ -z "${PANDOCK}" ]]; then
    PANDOCK=ebullient/pandoc-emoji:3.1
fi
# Git commit information (SHA, date, repo url)
DATE=$(date "+%Y-%m-%d")
if [[ "${IS_PR}" == "true" ]]; then
    FOOTER="${DATE} ✧ ${GITHUB_REF}"
elif [[ -z "${GITHUB_SHA}" ]]; then
    GITHUB_SHA=$(git rev-parse --short HEAD)
    FOOTER="${DATE} ✧ commit ${GITHUB_SHA}"
fi
if [[ -z "${GIT_COMMIT}" ]]; then
    GIT_COMMIT=$(git rev-parse HEAD)
fi
URL=$(gh repo view --json url --jq '.url')/

echo URL=${URL}
echo GIT_COMMIT=${GIT_COMMIT}
echo GITHUB_SHA=${GITHUB_SHA}

# Docker command and arguments
ARGS="--rm -e TERM -e HOME=/data -u $(id -u):$(id -g) -v $(pwd):/data -w /data"
if [[ "$OSTYPE" == "darwin"* ]]; then
    ARGS="$ARGS --platform linux/amd64"
fi
if [[ "${DRY_RUN}" == "true" ]]; then
    DOCKER="echo docker"
elif [[ -z "${DOCKER}" ]]; then
    DOCKER=docker
fi

TO_CMD=${1:-nope}
# Invoke command in the pandock container with common docker arguments
if [[ "${TO_CMD}" == "sh" ]]; then
    ${DOCKER} run ${ARGS} --entrypoint="" "${PANDOCK}" "$@"
    exit 0
fi
# Invoke pandoc with common docker arguments
if [[ "${TO_CMD}" != "nope" ]]; then
    ${DOCKER} run ${ARGS} "${PANDOCK}" "$@"
    exit 0
fi

# Convert markdown to PDF with an appended changelog
function to_pdf_pattern() {
    local tmpout=output/tmp/${1}
    shift
    local pdfout=output/public/${1}
    shift
    local relative_path=${1}
    shift

    mkdir -p "${tmpout}"
    rm -f "${tmpout}"/*
    rm -f "${pdfout}"

    # Use mounted volume paths
    run_pdf --pdf-engine-opt=-output-dir="./${tmpout}" \
        --pdf-engine-opt=-outdir="./${tmpout}" \
        -o "./${pdfout}" \
        -V dirname:"${relative_path}" \
        "$@"
}

# Convert markdown to PDF with an appended changelog
# to_pdf ./TRADEMARKS.md trademark-list ./ "Commonhaus Foundation Trademark List"
function to_pdf() {
    if [[ ! -f "${1}" ]]; then
        echo "No source file found at ${1}"
        exit 1
    fi
    local source=${1}
    shift
    local basename=${1}
    shift
    local relative_path=${1}
    shift
    local title=${1}
    shift

    local tmpout=output/tmp/${basename}
    mkdir -p "${tmpout}"
    rm -f "${tmpout}"/*
    local pdfout=output/public/${basename}.pdf
    rm -f "${pdfout}"

    # Use mounted volume paths
    run_pdf --pdf-engine-opt=-output-dir="./${tmpout}" \
        --pdf-engine-opt=-outdir="./${tmpout}" \
        -o "./${pdfout}" \
        -M title:"${title}" \
        -V dirname:"${relative_path}" \
        "${source}"
}

# Convert markdown to PDF
function run_pdf() {
    ${DOCKER} run ${ARGS} \
        "${PANDOCK}" \
        -H ./.pandoc/header.tex \
        -A ./.pandoc/afterBody.tex \
        -d ./.pandoc/bylaws.yaml \
        -M date-meta:"$(date +%B\ %d,\ %Y)" \
        --metadata-file=CONTACTS.yaml \
        -V footer-left:"${FOOTER}" \
        -V github:"${URL}blob/${GIT_COMMIT}/" \
        "$@"

    echo "$?"
}

mkdir -p output/tmp
mkdir -p output/public

## BYLAWS

# Sorted order of files for Bylaws
BYLAWS=(
    ./bylaws/0-preface.md
    ./bylaws/1-purpose.md
    ./bylaws/2-cf-membership.md
    ./bylaws/3-cf-council.md
    ./bylaws/4-cf-advisory-board.md
    ./bylaws/5-decision-making.md
    ./bylaws/6-notice-records.md
    ./bylaws/7-indemnification-dissolution.md
    ./bylaws/8-amendments.md
)

# Verify that bylaws files exist
for x in "${BYLAWS[@]}"; do
    if [[ ! -f ${x} ]]; then
        echo "No file found at ${x}"
        exit 1
    fi
done

# Convert bylaws to PDF
to_pdf_pattern \
    bylaws \
    "cf-bylaws.pdf" \
    "./bylaws/" \
    -M title:"Commonhaus Foundation Bylaws" \
    "${BYLAWS[@]}"

## POLICIES

# Convert individual policy to PDF
function to_policy_pdf() {
    if [[ ! -f "./policies/${1}.md" ]]; then
        echo "No policy found at ./policies/${1}.md"
        exit 1
    fi
    to_pdf_pattern \
        "${1}" \
        "${1}.pdf" \
        "./policies/" \
        -M title:"Commonhaus Foundation ${2} Policy" \
        "./policies/${1}.md"
}

# Convert all policies to PDF
to_policy_pdf code-of-conduct "Code of Conduct"
to_policy_pdf conflict-of-interest "Conflict of Interest"
to_policy_pdf ip-policy "Intellectual Property"
to_policy_pdf security-policy "Security Vulnerability Reporting"
to_policy_pdf succession-plan "Continuity and Administrative Access"
to_policy_pdf trademark-policy "Trademark"

to_pdf ./TRADEMARKS.md trademark-list ./ "Commonhaus Foundation Trademark List"

## AGREEMENTS

function to_agreement_pdf() {
    if [[ ! -f "./agreements/${1}.md" ]]; then
        echo "No agreement found at ./agreements/${1}.md"
        exit 1
    fi
    local name=$(basename ${1})
    sed -E 's/\[Insert [^]]* here\]/______________________________________/g' \
            "./agreements/${1}.md" > "./output/tmp/${name}.md"

    to_pdf_pattern \
        "${1}" \
        "$(basename ${1}).pdf" \
        "./agreements/$(dirname ${1})/" \
        -M title:"Commonhaus Foundation ${2} Agreeement" \
        "./output/tmp/${name}.md"
}

# Very redundant, but .. whatever. ;)
to_agreement_pdf bootstrapping/bootstrapping Bootstrapping

ls -al output/public