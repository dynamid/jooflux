# Contributing

We welcome contributions.

We won't accept patches sent by email or even modification in the form of
*whole modified files*.

Any contribution should be offered through
[a pull request on our main repository](https://github.com/dynamid/jooflux/pulls).

## Legalese

Please note that:

1. we reserve the right to refuse contributions, and
2. making a pull request implicitly means that you are legally entitled to do so, and
3. you implicitly accept the project license.

You either:

1. implicitly transfer the copyright to the project owners, or you
2. explicitly mention your licensing choices as part of the pull request.

## Commits

Commits should be explicit and provide a discussion of the changes. Think of each commit as the
discussion you would put in an email if you were to convince others that this is indeed useful.

A bad commit would be:

    bug fix

or

    fix the bug with resolution

Instead, a good commit would be:

    Introducing a Registration class
    
    The previous implementation had a simple direct
    `(target name -> Set<CallSite>` mapping.
    
    The registration class encapsulates the target key, original invocation type and
    call sites. The rationale for this change is to enable the external introspection
    of the original invocation type of a call site (e.g., `virtual`, `static`, etc).
    
    Tools such as remote shells can take advantage of this in user assistance like
    line completion.

## Preparing a pull request without bureaucracy

Preparing clean pull requests doesn't have to be complicated. We suggest that:

1. you fork the reference repository, and
2. you create a topic branch off the `master` branch, and
3. you make commits in this branch, and
4. you publish this branch publicly, and
5. you send us a pull request.

It is often the case that we need to develop things incrementally, in which cases commit messages
are hard to get clean and right. There is a simple solution for that:

1. work in a branch where you can make as many dirty commits as needed, then
2. squash all changes into a single commit with a polished message using either `git rebase i` or `git merge --squash`.

