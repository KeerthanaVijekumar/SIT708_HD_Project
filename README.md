# TicketMate AI

An Android application designed to support IT service desk analysts in managing and resolving support tickets efficiently using on-device AI.

## Overview

TicketMate AI leverages on-device Generative AI (Gemma-3 1B via MediaPipe) to analyse incoming support tickets, generate concise summaries, recommend resolution steps, and generate PowerShell automation scripts, all without sending any data to the cloud.

## Features

- **AI Ticket Analysis** — On-device Gemma-3 analyses ticket descriptions and generates summaries and troubleshooting steps
- **Script Generator** — Describe a task in plain English and get a PowerShell script generated on-device
- **Human-in-the-Loop** — All AI-generated content must be approved by the analyst before saving
- **Safety Handling** — Unsafe or destructive requests are flagged and refused
- **Ticket Management** — Create, view, and resolve IT support tickets
- **History** — View all resolved tickets with resolution notes
- **Fully Offline** — All AI inference runs on-device, no internet required

## Tech Stack

| Component | Technology |
|---|---|
| IDE | Android Studio |
| Language | Java |
| Architecture | MVVM |
| Local Database | Room |
| On-Device AI | MediaPipe LLM Inference API |
| AI Model | Gemma-3 1B (4-bit quantized) |
| UI | Material Design 3 |
| Navigation | Android Navigation Component |

## SDK Requirements

```
compileSdk:  36
targetSdk:   36
minSdk:      26
```

## Setup Instructions

### 1. Clone the repository

```
git clone https://github.com/KeerthanaVijekumar/SIT708_HD_Project.git
```

### 2. Open in Android Studio

- Open Android Studio
- Click File → Open
- Navigate to the cloned folder
- Wait for Gradle sync to complete

### 3. Install the Gemma-3 Model on your device

The AI model is not included in the repository due to its size (~555MB). You must manually push it to your device.

**Step 1 — Download the model**

Go to: `https://huggingface.co/litert-community/Gemma3-1B-IT`

Accept the licence and download:
```
Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task
```

**Step 2 — Connect your device via USB**

Enable USB Debugging:
```
Settings → About Phone → tap Build Number 7 times
Settings → Developer Options → USB Debugging → ON
```

**Step 3 — Push model to device using ADB**

```
cd C:\Users\<your-username>\AppData\Local\Android\Sdk\platform-tools

adb shell mkdir -p /data/local/tmp/llm/

adb push <path-to-downloaded-file>\Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task /data/local/tmp/llm/gemma3.task
```

**Step 4 — Verify**

```
adb shell ls /data/local/tmp/llm/
```

Should show: `gemma3.task`

### 4. Run the app

- Connect your physical Android device (8GB+ RAM recommended)
- Click Run ▶ in Android Studio
- Select your device

## How to Use

### Analyse a Ticket
1. Open the app — 5 sample tickets are pre-loaded
2. Tap any ticket to open it
3. Tap ** Analyse with AI**
4. Wait 10–20 seconds for on-device inference
5. Review the AI summary and suggested steps
6. Tap **✓ Approve & Save Notes** to save
7. Tap **Mark as Resolved** to close the ticket

### Generate a PowerShell Script
1. Tap the **Scripts** tab
2. Describe your task in plain English
   - e.g. "Enroll a new user to Entra ID"
   - e.g. "Clear the print spooler on a remote machine"
3. Tap ** Generate Script**
4. Review the generated script
5. Tap **✓ Approve & Copy to Clipboard**

### View History
1. Tap the **History** tab
2. View all resolved tickets with timestamps and resolution notes

## AI Integration Notes

### Model
- **Model:** Gemma-3 1B Instruct (4-bit quantized)
- **Format:** MediaPipe LiteRT `.task` file
- **Size:** ~555MB
- **Backend:** CPU inference via MediaPipe LLM Inference API

### Privacy
- All inference is performed **entirely on-device**
- No ticket data, descriptions, or generated content is sent to any server
- No internet connection required for AI features
- Suitable for use in secure/air-gapped environments

### Offline Capability
- Full AI functionality works without internet connection
- Only requires the model file to be pre-installed on the device

### Safety Handling
- Input safety check runs before every AI call
- Destructive commands (format drive, delete all, rm -rf etc.) are blocked
- Model is prompted to respond with `UNSAFE_REQUEST` for dangerous tasks
- All AI output requires human approval before being saved

## Architecture

```
UI Layer (Fragments + XML layouts)
        ↓
ViewModel Layer (LiveData + business logic)
        ↓
Repository Layer (single source of truth)
        ↓
    ┌───────────────────────┐
    │                       │
Room Database         LlamaHelper.java
(Local tickets)    (MediaPipe + Gemma-3)
```

## Testing

| Device | API Level | Purpose |
|---|---|---|
| Pixel 10 Pro (physical) | API 36 (Android 16) | Full AI feature testing |
| Pixel 6a (emulator) | API 35 (Android 15) | Compatibility testing |

## Android 16 Back Navigation Compatibility

This app uses the **Android Navigation Component** which implements `OnBackPressedDispatcher` internally. This makes the app fully compatible with Android 16 predictive back navigation out of the box — no additional configuration required.

## Known Issues

- MediaPipe `tasks-genai` library shows a 16KB alignment warning on newer devices during debug builds. This is a known upstream issue and does not affect functionality.
- First AI inference after app launch may take 15–25 seconds as the model loads into memory.

## Future Work

- Swap Gemma-3 for a larger model when device RAM allows
- Add RAG (Retrieval Augmented Generation) over historical ticket database
- Push notifications for AI-suggested ticket escalations
- Integration with enterprise ticketing systems (Jira, ServiceNow)
- Model caching optimisation for faster subsequent inferences

## Development Methodology

Agile/iterative approach with 4 weekly sprints:
- **Week 8:** Project setup, Room DB, navigation skeleton
- **Week 9:** AI integration, Gemma-3 model setup
- **Week 10:** Script generator, UI polish, safety handling
- **Week 11:** Emulator testing, presentation, submission

