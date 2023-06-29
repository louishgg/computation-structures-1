# Factorial Test Program

`programs/factorial_test.basm` is a 31-instruction Beta assembly program used to validate the machine. It first exercises arithmetic, bitwise, shift, jump, load, store, and branch behavior, then computes `factorial(3)`.

| Check | Expected result |
| --- | --- |
| Store input value | `memory[0] = 3` |
| Compute factorial | `memory[4] = 6` |

The compiled Logisim memory image is stored in `programs/factorial_test.basm.logisim-contents`.
