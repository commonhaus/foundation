#!/bin/bash -x

# Docker image for pandoc
if [[ -z "${PANDOCK}" ]]; then
    PANDOCK=ghcr.io/commonhaus/pandoc-pdf:3.1
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
    FOOTER="${DATE} ✧ commit ${GITHUB_SHA}"
    URL="${URL}blob/${GITHUB_SHA}/"
fi

echo URL=${URL}

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

function run_shell() {
    ${DOCKER} run ${ARGS} --entrypoint="" "${PANDOCK}" "$@"
}

function run_pandoc() {
    ${DOCKER} run ${ARGS} "${PANDOCK}" "$@"
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
        -d ./.pandoc/bylaws.yaml \
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
        -d ./.pandoc/agreements.yaml \
        -M date-meta:"$(date +%B\ %d,\ %Y)" \
        -V github:"${URL}" \
        -o "$1" \
        "$2"

    echo "$?"
}

mkdir -p output/tmp
mkdir -p output/public

## BYLAWS

# Sorted order of files for Bylaws
BYLAWS=(
    ./bylaws/1-preface.md
    ./bylaws/2-purpose.md
    ./bylaws/3-cf-membership.md
    ./bylaws/4-cf-council.md
    ./bylaws/5-cf-advisory-board.md
    ./bylaws/6-decision-making.md
    ./bylaws/7-notice-records.md
    ./bylaws/8-indemnification-dissolution.md
    ./bylaws/9-amendments.md
)

if [[ -z "${SKIP_BYLAWS}" ]]; then
    # Verify that bylaws files exist
    for x in "${BYLAWS[@]}"; do
        if [[ ! -f ${x} ]]; then
            echo "No file found at ${x}"
            exit 1
        fi
    done

    # # Convert bylaws to PDF
    to_pdf_pattern \
        bylaws \
        "cf-bylaws.pdf" \
        "./bylaws/" \
        -M title:"Bylaws" \
        "${BYLAWS[@]}"
fi

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
        -M title:"${2} Policy" \
        "./policies/${1}.md"
}

if [[ -z "${SKIP_POLICIES}" ]]; then
    # Convert all policies to PDF
    to_policy_pdf code-of-conduct "Code of Conduct"
    to_policy_pdf conflict-of-interest "Conflict of Interest"
    to_policy_pdf ip-policy "Intellectual Property"
    to_policy_pdf trademark-policy "Trademark"

    to_pdf ./TRADEMARKS.md trademark-list ./ "Trademark List"
fi

## AGREEMENTS

function to_agreement_doc() {
    local input=${1}
    if [[ ! -f "./agreements/${input}.md" ]]; then
        echo "No agreement found at ./agreements/${input}.md"
        exit 1
    fi
    local output=${2}
    if [[ -z "${output}" ]]; then
        output=$(basename ${input})
    fi
    run_docx \
        "./output/public/${output}.docx" \
        "./agreements/${input}.md"
}

if [[ -z "${SKIP_AGREEMENTS}" ]]; then
    to_agreement_doc bootstrapping/bootstrapping bootstrapping-agreement
    # to_agreement_doc project-contribution/asset-transfer-agreement
    # to_agreement_doc project-contribution/fiscal-sponsorship-agreement
    # to_agreement_doc project-contribution/fiscal-sponsorship-terms-and-conditions
fi

ls -al output/public