# DiME Command-line Tool

Command-line tool for DiME (Data Integrity Message Envelope) data format used to build Application-based Public-Key Infrastructures (APKIs).

This is till in development, although much functionality is working.

The tool may be used for:
- Generate keys
- Generate identity issuing requests (IIR)
- Issue identities
	+ Self-issuing
	+ From IIRs
- Verify DiME items using a key or identity

## Usage

Show help:
```
dimetool --help
```

Generate a key:
```
dimetool key --cap sign
```
