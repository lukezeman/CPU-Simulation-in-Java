/* Author: Luke Zeman
 * Assignment: CSC 252 Sim 4
 * Date: 3/26/2025
 * 
 * Description: Simulates a single-cycle CPU, by decoding instructions and using the ALU.
 */

public class Sim4 {

	public int signExtend16to32(int val16) {
		/* This method sign extends a 16-bit integer to a 32-bit integer.
		 */
		if ((val16 & 0x8000) != 0)
			return val16 | 0xFFFF0000;
		else
			return val16;
		
	}

	public void extractInstructionFields(int instruction, InstructionFields fieldsOut) {
		/* Sets various instruction fields based on the given instruction integer.
		 */
		fieldsOut.opcode = (instruction >> 26) & 0x3F;
		fieldsOut.rs = (instruction >> 21) & 0x1F;
		fieldsOut.rt = (instruction >> 16) & 0x1F;
		fieldsOut.rd = (instruction >> 11) & 0x1F;
		fieldsOut.shamt = (instruction >> 6) & 0x1F;
		fieldsOut.funct = instruction & 0x3F;
		fieldsOut.imm16 = instruction & 0xFFFF;
		fieldsOut.imm32 = signExtend16to32(fieldsOut.imm16);
		fieldsOut.address = instruction & 0x03FFFFFF;
	}

	public int fillCPUControl(InstructionFields fields, CPUControl controlOut) {
		/* Sets CPU control bits based on the instruction format, and the instruction itself.
		 */
		int opcode = fields.opcode;
		
		// R-Format instruction, opcode = 0.
		if (opcode == 0) {
			controlOut.regDst = 1;
			controlOut.regWrite = 1;
			controlOut.memRead = 0;
			controlOut.memWrite = 0;
			controlOut.memToReg = 0;
			controlOut.branch = 0;
			controlOut.jump = 0;
			controlOut.ALUsrc = 0;

			switch (fields.funct) {
				case 0:		// sll (extra instruction 1)
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 5;
					break;
				case 32:	// add
				case 33: 	// addu
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 2;
					break;
				case 34: 	// sub
				case 35: 	// subu
					controlOut.ALU.bNegate = 1;
					controlOut.ALU.op = 2;
					break;
				case 36: 	// and
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 0;
					break;
				case 37: 	// or
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 1;
					break;
				case 38: 	// xor
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 4;
					break;
				case 42: 	// slt
					controlOut.ALU.bNegate = 1;
					controlOut.ALU.op = 3;
					break;
				default: // not recognized
					controlOut.regDst = 0;
					controlOut.regWrite = 0;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 0;
					controlOut.ALUsrc = 0;

					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 0;
					return 0;
			}

		}
		// I-Format instruction, opcode > 0.
		else {
			switch (opcode) {
				case 8: 	// addi
				case 9:		// addiu
					controlOut.regDst = 0;
					controlOut.regWrite = 1;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 2;
					controlOut.ALUsrc = 1;
					break;
				case 10: 	// slti
					controlOut.regDst = 0;
					controlOut.regWrite = 1;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 1;
					controlOut.ALU.op = 3;
					controlOut.ALUsrc = 1;
					break;
				case 12: 	// andi (extra instruction 2)
					controlOut.regDst = 0;
					controlOut.regWrite = 1;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 0;
					controlOut.ALUsrc = 1;
					// Uses an extra control bit.
					controlOut.extra1 = 1;
					break;
				case 15: 	// lui (extra instruction 3)
					controlOut.regDst = 0;
					controlOut.regWrite = 1;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 6;
					controlOut.ALUsrc = 1;
					// Uses an extra control bit.
					controlOut.extra2 = 1;
					break;
				case 35:	// lw
					controlOut.regDst = 0;
					controlOut.regWrite = 1;
					controlOut.memRead = 1;
					controlOut.memWrite = 0;
					controlOut.memToReg = 1;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 2;
					controlOut.ALUsrc = 1;
					break;
				case 43:	// sw
					controlOut.regDst = 0;
					controlOut.regWrite = 0;
					controlOut.memRead = 0;
					controlOut.memWrite = 1;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 2;
					controlOut.ALUsrc = 1;
					break;
				case 4:		// beq
					controlOut.regDst = 0;
					controlOut.regWrite = 0;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 1;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 1;
					controlOut.ALU.op = 2;
					controlOut.ALUsrc = 0;
					break;
				case 2:		// j
					controlOut.regDst = 0;
					controlOut.regWrite = 0;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 1;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 0;
					controlOut.ALUsrc = 0;
					break;
				default: // not recognized
					controlOut.regDst = 0;
					controlOut.regWrite = 0;
					controlOut.memRead = 0;
					controlOut.memWrite = 0;
					controlOut.memToReg = 0;
					controlOut.branch = 0;
					controlOut.jump = 0;
					controlOut.ALU.bNegate = 0;
					controlOut.ALU.op = 0;
					controlOut.ALUsrc = 0;
					return 0;
			}
		}
        return 1;

	}
    
    // Method signatures corresponding to function prototypes in sim4.h
	public int getInstruction(int curPC, int[] instructionMemory) {
		/* This method retrieves an instruction from memory given the current Program Counter.
		 */
		int index = curPC / 4;
		return instructionMemory[index];

	}

	public int getALUinput1(CPUControl controlIn, InstructionFields fieldsIn,
                                   int rsVal, int rtVal, int reg32, int reg33, int oldPC) {
		/* This method returns the register value to be used as the first ALU input.
		 */
		if (controlIn.ALU.op == 5) { // sll
			return rtVal;
		} else {
			return rsVal;
		}
	}

	public int getALUinput2(CPUControl controlIn, InstructionFields fieldsIn,
                                   int rsVal, int rtVal, int reg32, int reg33, int oldPC) {
		/* This method selects the second operand for the ALU based on the instruction.
		 */
		if (controlIn.ALU.op == 5) { 			// sll
			return fieldsIn.shamt;
		} else if (controlIn.extra1 == 1) {		// andi
			return fieldsIn.imm16;
		} else if (controlIn.extra2 == 1) {		// lui
			return fieldsIn.imm16;
		} else if (controlIn.ALUsrc == 0) {
			return rtVal;
		} else {
			return fieldsIn.imm32;
		}
	}

	public void executeALU(CPUControl controlIn, int input1, int input2, ALUResult aluResultOut) {
		int result = 0;
		/* This method executes the ALU given the control bits and inputs.
		 */
		switch (controlIn.ALU.op) {
			case 0: // and/andi
				result = input1 & input2;
				break;
			case 1: // or
				result = input1 | input2;
				break;
			case 2: // add/addu/addi/addiu/sub/subu
				if (controlIn.ALU.bNegate == 1) {
					result = input1 - input2;
				} else {
					result = input1 + input2;
				}
				break;
			case 3:	// slt/slti
				boolean temp = input1 < input2;
				if (temp) result = 1;
				break;
			case 4: // xor
				result = input1 ^ input2;
				break;
			case 5: // sll
				result = input1 << input2;
				break;
			case 6: // lui
				result = (input2 & 0xFFFF) << 16;
				break;
			default: // not recognized
				result = 0;
		}

		aluResultOut.result = result;
		if (result == 0) aluResultOut.zero = 1;
		else aluResultOut.zero = 0;

	}

	public void executeMEM(CPUControl controlIn, ALUResult aluResultIn,
                                  int rsVal, int rtVal, int[] memory, MemResult resultOut) {
		/* This method simulates memory and reads or writes to it based on the control bits.
		 */
		resultOut.readVal = 0;
		int index = aluResultIn.result / 4;

		if (controlIn.memRead == 1) {
			resultOut.readVal = memory[index];
		}
		if (controlIn.memWrite == 1) {
			memory[index] = rtVal;
		}

	}

	public int getNextPC(InstructionFields fields, CPUControl controlIn, int aluZero,
                                int rsVal, int rtVal, int oldPC) {
		/* This method returns the next Program Counter depending on the instruction.
		 */
		int nextPC = oldPC + 4;

		// If the instruction is j.
		if (controlIn.jump == 1) {
			nextPC = ((oldPC + 4) & 0xF0000000) | (fields.address << 2);
		}
		// If the instruction is beq.
		else if (controlIn.branch == 1 && aluZero == 1) {
			nextPC = oldPC + 4 + (fields.imm32 << 2);
		}

        return nextPC;
	}

	public void executeUpdateRegs(InstructionFields fields, CPUControl controlIn,
                                         ALUResult aluResultIn, MemResult memResultIn, int[] regs) {
		/* This method updates registers if certain control bits are set.
		 */
		if (controlIn.regWrite == 1) {
			int dstReg;
			// Determine destination register.
			if (controlIn.regDst == 1) {
				// R-type.
				dstReg = fields.rd;
			} else {
				// I-type.
				dstReg = fields.rt;
			}

			int writeValue;
			// Get value to write.
			if (controlIn.memToReg == 1) {
				writeValue = memResultIn.readVal;
			} else {
				writeValue = aluResultIn.result;
			}

			regs[dstReg] = writeValue;
		}
	}

}