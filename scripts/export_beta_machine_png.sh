#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD_DIR="$(mktemp -d)"
trap 'rm -rf "$BUILD_DIR"' EXIT
EXPORT_CIRCUIT="$BUILD_DIR/beta_machine_export.circ"

javac -cp "$ROOT/tools/logisim.jar" -d "$BUILD_DIR" "$ROOT/scripts/ExportLogisimCircuit.java"
cp "$ROOT/circuits/beta_machine.circ" "$EXPORT_CIRCUIT"
perl -0pi -e 's/<a name="appear" val="center"\/>/<a name="appear" val="legacy"\/>/g' "$EXPORT_CIRCUIT"

java -Djava.awt.headless=true -cp "$BUILD_DIR:$ROOT/tools/logisim.jar" ExportLogisimCircuit \
  "$EXPORT_CIRCUIT" \
  main \
  "$ROOT/assets/beta-machine.png" \
  2.0
