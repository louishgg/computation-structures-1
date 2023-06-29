#!/usr/bin/env python3
"""Generate the Logisim control ROM image for the Beta machine."""

from argparse import ArgumentParser
from pathlib import Path


CONTROL_LOGIC = """v2.0 raw
24*0 1a0 10c 0 82 0 80 80
0 1d0 2d0 3d0 4d0 ad0 8d0 9d0
0 5d0 6d0 7d0 0 bd0 cd0 dd0
0 190 290 390 490 a90 890 980
0 590 690 790 0 b90 c90 d90"""


def default_output() -> Path:
    return Path(__file__).resolve().parents[1] / "roms" / "control_logic"


def main() -> None:
    parser = ArgumentParser(description="Generate the Logisim control_logic ROM file.")
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        default=default_output(),
        help="Output path. Defaults to roms/control_logic.",
    )
    args = parser.parse_args()

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(CONTROL_LOGIC, encoding="utf-8")
    print(f"Wrote {args.output}")


if __name__ == "__main__":
    main()
