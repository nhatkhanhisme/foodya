<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **foodya** (3213 symbols, 6871 relationships, 265 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/foodya/context` | Codebase overview, check index freshness |
| `gitnexus://repo/foodya/clusters` | All functional areas |
| `gitnexus://repo/foodya/processes` | All execution flows |
| `gitnexus://repo/foodya/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |
| Work in the Controller area (106 symbols) | `.claude/skills/generated/controller/SKILL.md` |
| Work in the Repository area (61 symbols) | `.claude/skills/generated/repository/SKILL.md` |
| Work in the Service area (53 symbols) | `.claude/skills/generated/service/SKILL.md` |
| Work in the Model area (36 symbols) | `.claude/skills/generated/model/SKILL.md` |
| Work in the Customer area (29 symbols) | `.claude/skills/generated/customer/SKILL.md` |
| Work in the Merchant area (20 symbols) | `.claude/skills/generated/merchant/SKILL.md` |
| Work in the Components area (20 symbols) | `.claude/skills/generated/components/SKILL.md` |
| Work in the Dashboard area (18 symbols) | `.claude/skills/generated/dashboard/SKILL.md` |
| Work in the Restaurant area (12 symbols) | `.claude/skills/generated/restaurant/SKILL.md` |
| Work in the Local area (10 symbols) | `.claude/skills/generated/local/SKILL.md` |
| Work in the Jwt area (8 symbols) | `.claude/skills/generated/jwt/SKILL.md` |
| Work in the Middleware area (7 symbols) | `.claude/skills/generated/middleware/SKILL.md` |
| Work in the Menu area (7 symbols) | `.claude/skills/generated/menu/SKILL.md` |
| Work in the Remote area (6 symbols) | `.claude/skills/generated/remote/SKILL.md` |
| Work in the Auth area (6 symbols) | `.claude/skills/generated/auth/SKILL.md` |
| Work in the Config area (3 symbols) | `.claude/skills/generated/config/SKILL.md` |
| Work in the Cluster_83 area (3 symbols) | `.claude/skills/generated/cluster-83/SKILL.md` |
| Work in the Security area (3 symbols) | `.claude/skills/generated/security/SKILL.md` |

<!-- gitnexus:end -->
