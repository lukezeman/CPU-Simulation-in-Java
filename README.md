# CPU-Simulation-in-Java
This project is a simplified single-cycle CPU simulator written in Java for an academic computer organization course. The simulation models the core functionality of a basic MIPS processor and demonstrates how machine instructions are executed at the hardware level.

# Project Overview
- Decodes and executes 32-bit MIPS instructions
- Implements key components of the datapath:
---------------------------------------------
- Instruction decoding
- ALU operation and control
- Memory access
- Register writeback
- Program Counter updates
---------------------------------------------
- Supports both R-type and I-type instructions including:
---------------------------------------------
- add, sub, and, or, xor, sll, slt
- addi, slti, andi, lui
- lw, sw, beq, j
---------------------------------------------
# How It Works
1. Takes a 32-bit instruction and extracts fields like opcode, registers, and immediates
2. Determines the instruction type and sets control signals accordingly
3. Selects ALU inputs based on control and instruction format
4. Performs ALU computation and optional memory access
5. Updates registers and calculates the next Program Counter

# Concepts Practiced
- Instruction set architecture (ISA) design (MIPS)
- Bit manipulation and instruction parsing
- Control signal generation
- Single-cycle CPU operation modeling
- Data flow and register/memory updates
