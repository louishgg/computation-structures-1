#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD_DIR="$(mktemp -d)"
trap 'rm -rf "$BUILD_DIR"' EXIT
NORMAL_CIRCUIT="$BUILD_DIR/beta_machine_normal.circ"
LEGACY_CIRCUIT="$BUILD_DIR/beta_machine_legacy.circ"
OUTPUT_DIR="$ROOT/assets/circuits"

javac -cp "$ROOT/tools/logisim.jar" -d "$BUILD_DIR" "$ROOT/scripts/ExportLogisimCircuit.java"
cp "$ROOT/circuits/beta_machine.circ" "$NORMAL_CIRCUIT"
cp "$ROOT/circuits/beta_machine.circ" "$LEGACY_CIRCUIT"
perl -0pi -e 's/<a name="appear" val="center"\/>/<a name="appear" val="legacy"\/>/g' "$LEGACY_CIRCUIT"
mkdir -p "$OUTPUT_DIR"

circuits=(
  "ALU:alu:normal"
  "enhanced_comparator:enhanced-comparator:legacy"
  "program_counter:program-counter:normal"
  "register_file:register-file:normal"
  "instruction_memory:instruction-memory:legacy"
  "control_logic:control-logic:legacy"
  "data_memory:data-memory:legacy"
  "+4:plus-4:normal"
  "sext:sext:normal"
  "ORing_negation:oring-negation:legacy"
  "x4:x4:normal"
)

for entry in "${circuits[@]}"; do
  IFS=":" read -r circuit filename mode <<< "$entry"
  input="$NORMAL_CIRCUIT"
  if [[ "$mode" == "legacy" ]]; then
    input="$LEGACY_CIRCUIT"
  fi
  java -Djava.awt.headless=true -cp "$BUILD_DIR:$ROOT/tools/logisim.jar" ExportLogisimCircuit \
    "$input" \
    "$circuit" \
    "$OUTPUT_DIR/$filename.png" \
    2.0
done
