#!/usr/bin/env bash
# narae 브랜치의 커밋을 main 대비 하나씩 force-push 후 PR 생성 (순차 병합용)
# 사용법: gh auth login 후 ./scripts/push-prs-from-narae.sh

set -euo pipefail

cd "$(dirname "$0")/.."

if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI가 필요합니다: brew install gh && gh auth login"
  exit 1
fi

gh auth status >/dev/null 2>&1 || {
  echo "gh auth login 을 먼저 실행하세요."
  exit 1
}

git fetch origin main

COMMITS=()
MESSAGES=()
while IFS= read -r line; do COMMITS+=("$line"); done < <(git log --reverse --format=%H origin/main..narae --grep='^\[feat\]')
while IFS= read -r line; do MESSAGES+=("$line"); done < <(git log --reverse --format=%s origin/main..narae --grep='^\[feat\]')
if [ ${#COMMITS[@]} -eq 0 ]; then
  echo "origin/main..narae 사이에 [feat] 커밋이 없습니다."
  exit 1
fi

for i in "${!COMMITS[@]}"; do
  commit="${COMMITS[$i]}"
  title="${MESSAGES[$i]}"
  echo "=========================================="
  echo "PR $((i + 1))/${#COMMITS[@]}: $title"
  echo "커밋: $commit"
  git push -f origin "${commit}:refs/heads/narae"
  if gh pr list --head narae --base main --state open --json number -q '.[0].number' 2>/dev/null | grep -q .; then
    echo "이미 열린 narae→main PR이 있습니다. 병합 후 다시 실행하세요."
    gh pr list --head narae --base main --state open
    exit 1
  fi
  gh pr create --base main --head narae --title "$title" --body "$(cat <<EOF
## Summary
- $title

## Test plan
- [ ] Swagger에서 API 확인
- [ ] 로컬 빌드 및 동작 테스트
EOF
)"
  echo "PR 생성 완료. main에 병합한 뒤 스크립트를 다시 실행하면 다음 PR을 올립니다."
  exit 0
done

echo "모든 커밋에 대한 PR 처리가 끝났습니다."
