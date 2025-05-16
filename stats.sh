#!/bin/bash

BASHRC="$HOME/.bashrc"
FN_NAME="pstats"

# Sprawdź, czy funkcja już istnieje w ~/.bashrc
if grep -q "^\s*${FN_NAME}()" "$BASHRC"; then
    echo "Funkcja '${FN_NAME}' już istnieje w $BASHRC – nic nie dodano."
    exit 0
fi

# Dodaj funkcję pstats
cat >> "$BASHRC" << 'EOF'

# === Funkcja pstats ===
pstats() {
  RED='\033[0;31m'
  RESET='\033[0m'

  echo -n "1. Liczba klas: "
  class_count=$(grep -rho '\bclass\b' --include='*.java' . | wc -l)

  git ls-files '*.java' | xargs -n1 git blame --line-porcelain | \
    awk '/^author / {print $2}' | sort -u > authors.tmp
  author_count=$(wc -l < authors.tmp)

  if [ "$class_count" -lt $((7 * author_count)) ] && [ "$class_count" -lt 21 ]; then
    echo -e "${RED}${class_count}${RESET}"
  else
    echo "$class_count"
  fi

  echo "2. Liczba klas według autorów:"
  grep -rn '\bclass\b' --include='*.java' . > class_lines.tmp
  cut -d: -f1,2 class_lines.tmp | while IFS=: read -r file line; do
    git blame -L "$line","$line" --line-porcelain "$file" 2>/dev/null | \
      awk '/^author / {print $2; exit}'
  done | sort | uniq -c | sort -nr > class_by_author.tmp

  while read -r count author; do
    if [ "$count" -lt 7 ]; then
      echo -e "    ${RED}> $author $count${RESET}"
    else
      echo "$count $author"
    fi
  done < class_by_author.tmp

  rm class_lines.tmp class_by_author.tmp authors.tmp

  git ls-files '*.java' | xargs -n1 git blame --line-porcelain | \
    awk '/^author / {authors[$2]++} END {for (a in authors) print authors[a], a}' | \
    sort -nr > java_lines_by_author.txt

  total=$(awk '{sum+=$1} END {print sum}' java_lines_by_author.txt)
  echo "3. Całkowita liczba linii kodu: $total"
  echo "Autorzy i ich procentowy wkład:"

  while read -r count author; do
    percent=$(awk -v c="$count" -v t="$total" 'BEGIN {printf "%.2f", (c/t)*100}')
    if (( $(echo "$percent < 20" | bc -l) )); then
      echo -e "${RED}${author}: ${count}  (${percent}%)${RESET}"
    else
      echo "${author}: ${count}  (${percent}%)"
    fi
  done < java_lines_by_author.txt

  rm java_lines_by_author.txt
}
# === Koniec funkcji pstats ===

EOF

echo "Dodano funkcję 'pstats' do $BASHRC ✅"
exit

