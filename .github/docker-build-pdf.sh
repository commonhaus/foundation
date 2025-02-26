#!/bin/bash -x

# Docker image for pandoc
if [[ -z "${PANDOCK}" ]]; then
    PANDOCK=ghcr.io/commonhaus/pandoc-pdf:edge-3
fi

DATE=$(date "+%Y-%m-%d")
URL=$(gh repo view --json url --jq '.url')/

if [[ -z "${GITHUB_SHA}" ]]; then
    GITHUB_SHA=$(git rev-parse --short HEAD)
fi

echo GITHUB_SHA=${GITHUB_SHA}

# Git commit information (SHA, date, repo url)
if [[ "${IS_PR}" == "true" ]]; then
    FOOTER="${DATE} ✧ ${GITHUB_REF}"
    URL="${PR_URL}"
else
    FOOTER="${DATE}"
    URL="${URL}blob/${GITHUB_SHA}/"
fi

echo URL=${URL}

# Docker command and arguments
ARGS="--rm -e TERM -e HOME=/data -u $(id -u):$(id -g) -v $(pwd):/data -w /data"
if [[ "$OSTYPE" == "darwin"* ]]; then
    ARGS="$ARGS --platform linux/amd64"
elif [[ "$DOCKER" == "podman" ]] || docker version 2>/dev/null | grep -qi podman; then
    ARGS="$ARGS --userns=keep-id"
fi
# Set DOCKER if not already defined
if [[ -z "${DOCKER}" ]]; then
    DOCKER=docker
fi
if [[ "${DRY_RUN}" == "true" ]]; then
    DOCKER="echo ${DOCKER}"
fi

function run_shell() {
    ${DOCKER} run ${ARGS} --entrypoint="" "${PANDOCK}" "$@"
}

function run_pandoc() {
    ${DOCKER} run ${ARGS} "${PANDOCK}" "$@"
}

# Convert markdown to PDF
function run_pdf() {
    ${DOCKER} run ${ARGS} \
        "${PANDOCK}" \
        -d /commonhaus/pandoc/pdf-common.yaml \
        -M date-meta:"$(date +%B\ %d,\ %Y)" \
        -V footer-left:"${FOOTER}" \
        -V github:"${URL}" \
        "$@"

    echo "$?"
}

# Convert markdown to DOCX
function run_docx() {
    ${DOCKER} run ${ARGS} \
        "${PANDOCK}" \
        -M date-meta:"$(date +%B\ %d,\ %Y)" \
        -V github:"${URL}" \
        "$@"

    echo "$?"
}

for x in "$@"; do
  case "$x" in
    --help)
      echo "Usage: $0 [--skip-policies] [--skip-bylaws] [--skip-agreements]"
      echo "OR: $0 [pandoc command arguments]"
      echo "OR: $0 sh [command]"
      exit 0
      ;;
    --skip-policies)
      SKIP_POLICIES=true
      shift
      ;;
    --skip-bylaws)
      SKIP_BYLAWS=true
      shift
      ;;
    --skip-agreements)
      SKIP_AGREEMENTS=true
      shift
      ;;
    *)
      ;;
  esac
done

TO_CMD=${1:-noargs}
# Invoke command in the pandock container with common docker arguments
if [[ "${TO_CMD}" == "sh" ]]; then
    run_shell "$@"
    exit 0
fi
# Invoke pandoc with common docker arguments
if [[ "${TO_CMD}" != "noargs" ]]; then
    run_pandoc "$@"
    exit 0
fi

mkdir -p output/tmp
mkdir -p output/public

# Convert markdown to PDF with an appended changelog
# working-dir is used to resolve resources in the file
# to_pdf pdf-basename   cwd         sources+args
# to_pdf trademark-list ./          sources+args
# to_pdf x              ./policies  sources+args
# to_pdf cf-bylaws      ./bylaws    sources+args
function to_pdf() {
    local basename=${1}
    shift
    local relative_path=${1}
    shift

    local tmpout=./output/tmp/${basename}
    mkdir -p "${tmpout}"
    rm -f "${tmpout}"/*
    local pdfout=./output/public/${basename}.pdf
    rm -f "${pdfout}"

    # Use mounted volume paths
    run_pdf \
        --pdf-engine-opt=--output-directory="${tmpout}" \
        --pdf-engine-opt=-output-dir="${tmpout}" \
        --pdf-engine-opt=-outdir="${tmpout}" \
        -V dirname:"${relative_path}" \
        -o "${pdfout}" \
        "$@"
}

## BYLAWS

if [[ -z "${SKIP_BYLAWS}" ]]; then
    # Convert bylaws to PDF
    to_pdf \
        "cf-bylaws" \
        "./bylaws/" \
        --toc=true \
        --toc-depth=3 \
        -M "title:Bylaws" \
        ./bylaws/1-preface.md \
        ./bylaws/2-purpose.md \
        ./bylaws/3-cf-membership.md \
        ./bylaws/4-cf-council.md \
        ./bylaws/5-cf-advisory-board.md \
        ./bylaws/6-decision-making.md \
        ./bylaws/7-notice-records.md \
        ./bylaws/8-indemnification-dissolution.md \
        ./bylaws/9-amendments.md
fi

## POLICIES

# Convert individual policy to PDF
function to_policy_pdf() {
    if [[ ! -f "./policies/${1}.md" ]]; then
        echo "No policy found at ./policies/${1}.md"
        exit 1
    fi
    # to_pdf pdf-basename   working-dir title ... whatever else
    to_pdf \
        "${1}" \
        "./policies/" \
        -M "title:${2} Policy" \
        "./policies/${1}.md"
}

if [[ -z "${SKIP_POLICIES}" ]]; then
    # Convert policies to PDF
    # function    source file (no extension)  <title> Policy
    to_policy_pdf code-of-conduct             "Code of Conduct"
    to_policy_pdf conflict-of-interest        "Conflict of Interest"
    to_policy_pdf ip-policy                   "Intellectual Property"
    to_policy_pdf trademark-policy            "Trademark"

    # to_pdf pdf-file-name  cwd   the rest...
    to_pdf   trademark-list ./ \
        -M "title:Trademark List" \
        ./TRADEMARKS.md
fi

## AGREEMENTS

function to_agreement_doc() {
    shift
    local title=${1}
    shift

    local input=${1}
    if [[ ! -f "./agreements/${input}.md" ]]; then
        echo "No agreement found at ./agreements/${input}.md"
        exit 1
    fi
    local workingdir=$(basename "./agreements/${input}.md")
    local output=$(basename ${input})
    shift

    run_docx \
        -o "./output/public/${output}.docx" \
        -d "/commonhaus/pandoc/agreements-docx.yaml" \
        "./agreements/${input}.md"

    # to_pdf pdf-basename   working-dir whatever else
    to_pdf \
        "${output}" \
        "${workingdir}" \
        -d "/commonhaus/pandoc/agreements-pdf.yaml" \
        -M "title:${title}" \
        "$@" \
        "./agreements/${input}.md"
}

if [[ -z "${SKIP_AGREEMENTS}" ]]; then
    # function  is_draft   PDF title                      markdown source (no extension)    verbatim arguments
    to_agreement_doc false "Asset Transfer Agreement"     "project-contribution/asset-transfer-agreement" \
       -M bodyTitle:true
    to_agreement_doc false "Fiscal Sponsorship Agreement" "project-contribution/fiscal-sponsorship-agreement" \
       -M bodyTitle:true
    to_agreement_doc false "Sponsorship Agreement"        "sponsorship/sponsorship-agreement" \
       -M bodyTitle:true -M noHeaderBreak:true
fi

ls -al output/public